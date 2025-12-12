package at.fhv.Event.application.booking;

import at.fhv.Event.EventApplication;
import at.fhv.Event.TestSecurityBeansConfig;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.AudienceType;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.equipment.EquipmentSelection;
import at.fhv.Event.domain.model.exception.BookingValidationException;
import at.fhv.Event.domain.model.exception.EventFullyBookedException;
import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingJpaRepository;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.infrastructure.persistence.equipment.EventEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import at.fhv.Event.infrastructure.persistence.event.EventJpaRepository;
import at.fhv.Event.infrastructure.persistence.user.UserAccountEntity;
import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {EventApplication.class, TestSecurityBeansConfig.class})
@ActiveProfiles("test")
@Transactional
@Rollback

public class BookingIntegrationTest {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserAccountJpaRepository userAccountJpaRepository;

    @Autowired
    private BookEventService bookEventService;

    @Autowired
    private BookingJpaRepository bookingJpaRepository;

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @Autowired
    private EquipmentJpaRepository equipmentJpaRepository;


    private void ensureUser(String email, String first, String last) {
        UserAccountEntity u = new UserAccountEntity();
        u.setEmail(email);
        u.setFirstName(first);
        u.setLastName(last);
        u.setActive(true);
        u.setPasswordHash("test-hash");
        userAccountJpaRepository.saveAndFlush(u);
    }


    // 1) Happy Path: booking mit Equipment und Lagerbestandsreduzierung
    @Test
    void bookEvent_shouldCreateBookingAndReduceStock() {

        // GIVEN: Event + Equipment
        EventEntity event = new EventEntity();
        event.setTitle("Integration Test Event");
        event.setDescription("Test event for integration testing");
        event.setDate(LocalDate.now().plusDays(3));
        event.setStartTime(LocalTime.of(10, 0));
        event.setEndTime(LocalTime.of(12, 0));
        event.setMaxParticipants(10);
        event.setMinParticipants(1);
        event.setPrice(BigDecimal.valueOf(50));
        event.setCancelled(false);

        event = eventJpaRepository.save(event);

        EquipmentEntity tent = new EquipmentEntity();
        tent.setName("Tent");
        tent.setUnitPrice(BigDecimal.valueOf(20));
        tent.setStock(5);

        tent = equipmentJpaRepository.save(tent);

        int originalStock = tent.getStock();

        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(event.getId());
        request.setBookerFirstName("Max");
        request.setBookerLastName("Mustermann");
        request.setBookerEmail("max@example.com");
        request.setSeats(2);
        request.setAudience(AudienceType.INDIVIDUAL);
        request.setSpecialNotes("Integration test booking");

        EquipmentSelection selection = new EquipmentSelection();
        selection.setSelected(true);
        selection.setQuantity(2);

        request.setEquipment(Map.of(
                tent.getId(),
                selection
        ));

        EventEquipmentEntity eventEquipment = new EventEquipmentEntity(event, tent, false);
        event.addEquipment(eventEquipment);
        event = eventJpaRepository.save(event);
        eventJpaRepository.flush();

        ensureUser("max@example.com", "Max", "Mustermann");

        // WHEN: bookEvent über den echten Service
        BookingDTO bookingDTO = bookEventService.bookEvent(request);

        // THEN: Booking ist in der DB und Stock wurde reduziert
        assertNotNull(bookingDTO);
        assertNotNull(bookingDTO.getId());

        BookingEntity storedBooking =
                bookingJpaRepository.findById(bookingDTO.getId()).orElseThrow();

        assertEquals(event.getId(), storedBooking.getEventId());
        assertEquals("Max", storedBooking.getBookerFirstName());
        assertEquals("Mustermann", storedBooking.getBookerLastName());
        assertEquals("max@example.com", storedBooking.getBookerEmail());
        assertEquals(2, storedBooking.getSeats());

        EquipmentEntity reloadedTent =
                equipmentJpaRepository.findById(tent.getId()).orElseThrow();

        assertEquals(originalStock - 2, reloadedTent.getStock());
    }


    // 2) booking schlägt fehl, wenn nicht genug Equipment auf stock
    @Test
    void bookEvent_shouldFail_whenInsufficientEquipmentStock() {

        // GIVEN: Event + Equipment mit zu wenig Bestand (2 auf Lager, 5 im Request)
        EventEntity event = new EventEntity();
        event.setTitle("Stock Test Event");
        event.setDate(LocalDate.now().plusDays(5));
        event.setStartTime(LocalTime.of(14, 0));
        event.setEndTime(LocalTime.of(16, 0));
        event.setMaxParticipants(20);
        event.setMinParticipants(1);
        event.setPrice(BigDecimal.valueOf(10));
        event.setCancelled(false);

        event = eventJpaRepository.save(event);

        EquipmentEntity tent = new EquipmentEntity();
        tent.setName("Bow");
        tent.setUnitPrice(BigDecimal.valueOf(5));
        tent.setStock(2);

        tent = equipmentJpaRepository.save(tent);

        EventEquipmentEntity eventEquipment = new EventEquipmentEntity(event, tent, false);
        event.addEquipment(eventEquipment);
        event = eventJpaRepository.save(event);
        eventJpaRepository.flush();

        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(event.getId());
        request.setBookerFirstName("Lisa");
        request.setBookerLastName("Musterfrau");
        request.setBookerEmail("lisa@example.com");
        request.setSeats(5);
        request.setAudience(AudienceType.INDIVIDUAL);

        EquipmentSelection selection = new EquipmentSelection();
        selection.setSelected(true);
        selection.setQuantity(5);

        request.setEquipment(Map.of(
                tent.getId(),
                selection
        ));

        int originalStock = tent.getStock();

        ensureUser("lisa@example.com", "Lisa", "Musterfrau");

        // WHEN + THEN: Erwarte BookingValidationException
        BookingValidationException exception = assertThrows(
                BookingValidationException.class,
                () -> bookEventService.bookEvent(request),
                "BookingValidationException, da Validierung failed"
        );

        assertTrue(exception.getErrors().stream()
                .anyMatch(error ->
                        error.get_field().contains("equipments[") &&
                                error.get_message().contains("maximum available")
                ), "Der Fehler sollte spezifisch auf den unzureichenden Lagerbestand hinweisen (enthält 'maximum available').");


        // THEN: Prüfen, ob KEINE Buchung erstellt wurde und der Lagerbestand unverändert ist
        List<BookingEntity> bookings = bookingJpaRepository.findByEventId(event.getId());
        assertTrue(bookings.isEmpty(), "kein booking");

        EquipmentEntity reloadedTent = equipmentJpaRepository.findById(tent.getId()).orElseThrow();
        assertEquals(originalStock, reloadedTent.getStock(), "stock unverändert");
    }


    // 3) booking fails, wenn das Event ausgebucht
    @Test
    void bookEvent_shouldFail_whenEventIsFullyBooked() {

        // GIVEN: Event mit begrenzter capacity
        EventEntity event = new EventEntity();
        event.setTitle("Full Test Event");
        event.setDate(LocalDate.now().plusDays(10));
        event.setStartTime(LocalTime.of(18, 0));
        event.setEndTime(LocalTime.of(20, 0));
        event.setMaxParticipants(2);
        event.setMinParticipants(1);
        event.setPrice(BigDecimal.valueOf(100));
        event.setCancelled(false);

        event = eventJpaRepository.save(event);

        // 1. BUCHUNG: Bucht 2 seats (Event ist jetzt voll)
        BookingEntity initialBooking = new BookingEntity();
        initialBooking.setEventId(event.getId());
        initialBooking.setBookerFirstName("First");
        initialBooking.setBookerLastName("Booker");
        initialBooking.setBookerEmail("first@test.at");
        initialBooking.setSeats(1);
        initialBooking.setAudience(AudienceType.INDIVIDUAL);
        initialBooking.setTotalPrice(100.0);
        initialBooking.setStatus(BookingStatus.PAID);
        initialBooking.setCreatedAt(java.time.Instant.now());
        initialBooking.setPaymentStatus(at.fhv.Event.domain.model.payment.PaymentStatus.PAID);

        bookingJpaRepository.save(initialBooking);

        // Flush durchführen, um sicherzustellen, dass die DB-Zahlen aktuell sind
        bookingJpaRepository.flush();


        // Request für 2. Buchung
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(event.getId());
        request.setBookerFirstName("Second");
        request.setBookerLastName("Booker");
        request.setBookerEmail("second@test.at");
        request.setSeats(1);
        request.setAudience(AudienceType.INDIVIDUAL);
        request.setEquipment(Map.of());
        request.setParticipants(List.of());

        ensureUser("first@test.at", "First", "Booker");
        ensureUser("second@test.at", "Second", "Booker");

        // WHEN + THEN: Der Service sollte EventFullyBookedException werfen
        EventFullyBookedException exception = assertThrows(
                EventFullyBookedException.class,
                () -> bookEventService.bookEvent(request),
                "EventFullyBookedException, da Event voll"
        );

        // THEN: Prüfen der Fehlermeldungsdetails
        assertEquals(event.getId(), exception.get_eventId());
        assertEquals(1, exception.get_requestedSeats());
        assertEquals(0, exception.get_availableSeats(), "0 seats verfügbar");

        // THEN: Es sollte nur die erste Buchung existieren
        List<BookingEntity> bookings = bookingJpaRepository.findByEventId(event.getId());
        assertEquals(1, bookings.size(), "nur 1. booking in db");
    }
}