package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.BookingRequestMapper;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.exception.*;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Test cases (Application layer):
// 1) updatePaymentMethod: setzt paymentMethod, paymentStatus, booking status, speichert Booking, gibt DTO zurück
// 2) updatePaymentMethod: invalid paymentMethod → PaymentProcessingException, kein save(), kein Mapping

// 3) getDTOById: Booking existiert → BookingDTO wird zurückgegeben, Repo & Mapper werden aufgerufen
// 4) getDTOById: Booking existiert ned → BookingNotFoundException, Mapper wird ned aufgerufen

// 5) bookEvent: validator liefert Fehler → BookingValidationException, kein save()
// 6) bookEvent: event gibts ned → EventNotFoundException, validator/save/mapping werden ned aufgerufen

// 7) bookEvent: event is cancelled → IllegalStateException, kein save(), kein validator
// 8) bookEvent: event ist bereits vorbei → IllegalStateException, kein save
// 9) bookEvent: event ist voll → EventFullyBookedException

// 10) updateBooking: booking wird upgedatet wenn der input valid ist
// 11) updateBooking: IllegalStateException wenn das Event cancelled ist
// 12) updateBooking: BookingValidationException wenn validation fehlerhaft ist - kein save
// 13) updateBooking: FullyBookedException wenn Event voll und Customer will weiteren Participant adden


@ExtendWith(MockitoExtension.class)
class BookEventServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private BookingRequestMapper bookingRequestMapper;

    @Mock
    private BookingMapperDTO bookingMapperDTO;

    @Mock
    private BookingValidator bookingValidator;

    @InjectMocks
    private BookEventService bookEventService;

    // hilfsmethode, erstellt eine gültige booking
    private Booking createBooking(Long bookingId) {
        Booking booking = new Booking(
                42L,
                "Max",
                "Mustermann",
                "max@example.com",
                2,
                AudienceType.INDIVIDUAL,
                BookingStatus.PENDING,
                PaymentStatus.UNPAID,
                null,
                null,
                0.0,
                0.0,
                null,
                List.of(),         // participants
                new ArrayList<>()  // equipment
        );
        booking.setId(bookingId);
        return booking;
    }

    private CreateBookingRequest createValidRequest() {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setEventId(42L);
        req.setBookerFirstName("Max");
        req.setBookerLastName("Mustermann");
        req.setBookerEmail("max@example.com");
        req.setSeats(2);
        req.setAudience(AudienceType.INDIVIDUAL);
        req.setVoucherCode(null);
        req.setSpecialNotes(null);
        req.setPaymentMethod(PaymentMethod.ON_SITE);
        req.setEquipment(Map.of());
        req.setParticipants(List.of());
        req.setDiscountPercent(null);
        return req;
    }

// 1) updatePaymentMethod: setzt gültige paymentMethod, paymentStatus, booking status, speichert Booking, gibt DTO zurück
    @Test
    void updatePaymentMethod_shouldSetPaymentFieldsAndSaveBooking() {
        //given
        Long bookingId = 1L;
        String paymentMethodName = "PAYPAL";

        Booking existingBooking = createBooking(bookingId);
        Booking savedBooking = existingBooking; // im Test reicht es, wenn save dasselbe Objekt zurückgibt
        BookingDTO expectedDto = new BookingDTO();
        expectedDto.setId(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(existingBooking)).thenReturn(savedBooking);
        when(bookingMapperDTO.toDTO(savedBooking)).thenReturn(expectedDto);

        //then
        BookingDTO result = bookEventService.updatePaymentMethod(bookingId, paymentMethodName);

        assertEquals(PaymentMethod.PAYPAL, existingBooking.getPaymentMethod());
        assertEquals(PaymentStatus.PAID, existingBooking.getPaymentStatus());
        assertEquals(BookingStatus.CONFIRMED, existingBooking.getStatus());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(existingBooking);
        verify(bookingMapperDTO, times(1)).toDTO(savedBooking);

        assertSame(expectedDto, result);
    }

// 2) updatePaymentMethod: invalid paymentMethod → PaymentProcessingException, kein save(), kein Mapping
    @Test
    void updatePaymentMethod_shouldThrowPaymentProcessingException_forInvalidPaymentMethod() {
        // given
        Long bookingId = 2L;
        String invalidPaymentMethod = "apple";

        Booking existingBooking = createBooking(bookingId);

        // Booking wird gefunden, damit wir wirklich bis parsePaymentMethod kommen
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));

        PaymentProcessingException exception = assertThrows(
                PaymentProcessingException.class,
                () -> bookEventService.updatePaymentMethod(bookingId, invalidPaymentMethod)
        );

        // fehlermeldung checken (damit klar ist, warums crasht)
        assertTrue(exception.getMessage().contains("Invalid payment method"));

        // then: save darf nicht aufgerufen werden, weil payment ungültig
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoInteractions(bookingMapperDTO);
    }

// 3) getDTOById: Booking existiert → BookingDTO wird zurückgegeben, Repo & Mapper werden aufgerufen
    @Test
    void getDTOById_shouldReturnDto_whenBookingExists() {
        // Given: booking existiert und Repo & Mapper sind vorbereitet
        Long bookingId = 10L;
        Booking booking = createBooking(bookingId);

        BookingDTO dto = new BookingDTO();
        dto.setId(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapperDTO.toDTO(booking)).thenReturn(dto);

        // When: Methode wird aufgerufen
        BookingDTO result = bookEventService.getDTOById(bookingId);

        // Then: korrektes DTO zurück, Repo & Mapper wurden einmal verwendet
        assertSame(dto, result);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingMapperDTO, times(1)).toDTO(booking);
        verifyNoMoreInteractions(bookingRepository, bookingMapperDTO);
    }

// 4) getDTOById: Booking existiert ned → BookingNotFoundException, Mapper wird ned aufgerufen
    @Test
    void getDTOById_shouldThrowBookingNotFound_whenBookingDoesNotExist() {
        // Given: Repo liefert kein Booking
        Long bookingId = 99L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When + Then: Exception wird erwartet, Mapper darf nicht aufgerufen werden
        assertThrows(
                BookingNotFoundException.class,
                () -> bookEventService.getDTOById(bookingId)
        );

        verify(bookingRepository, times(1)).findById(bookingId);
        verifyNoInteractions(bookingMapperDTO);
    }

    // 5) bookEvent: validator liefert Fehler → BookingValidationException, kein save()
    @Test
    void bookEvent_shouldThrowBookingValidationException_whenValidatorReturnsErrors() {
        // Given
        CreateBookingRequest request = createValidRequest();

        Event mockEvent = mock(Event.class);
        when(mockEvent.getCancelled()).thenReturn(false);
        when(mockEvent.getDate()).thenReturn(java.time.LocalDate.now().plusDays(1));
        when(mockEvent.getStartTime()).thenReturn(java.time.LocalTime.NOON);
        when(mockEvent.getId()).thenReturn(42L);
        when(mockEvent.getMaxParticipants()).thenReturn(10);
        when(mockEvent.getMinParticipants()).thenReturn(0);

        // Event wird geladen
        when(bookingRepository.loadEventForBooking(42L)).thenReturn(mockEvent);

        // Seats / Capacity
        when(bookingRepository.countOccupiedSeatsForEvent(42L)).thenReturn(0);

        // Validator liefert ECHTEN ValidationError
        ValidationError error = new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                "seats",
                "Seats invalid",
                "2"
        );

        when(bookingValidator.validate(
                eq(request),
                eq(mockEvent),
                eq(Map.of()),
                eq(0)
        )).thenReturn(List.of(error));

        // When + Then: Soll BookingValidationException werfen
        assertThrows(
                BookingValidationException.class,
                () -> bookEventService.bookEvent(request)
        );

        // kein save(), kein DTO
        verify(bookingRepository, never()).save(any());
        verifyNoInteractions(bookingMapperDTO);
    }

    // 6) bookEvent: event gibts nicht → EventNotFoundException, validator/save/mapping werden ned aufgerufen
    @Test
    void bookEvent_shouldThrowEventNotFoundException_whenEventDoesNotExist() {
        // given: request mit eventId, repo wirft beim Laden vom Event eine Exception
        CreateBookingRequest request = createValidRequest();
        Long eventId = request.getEventId();

        // loadEventForBooking wird vom Service in loadEvent() verwendet
        when(bookingRepository.loadEventForBooking(eventId))
                .thenThrow(new RuntimeException("event not found"));

        // when + then: service soll EventNotFoundException werfen
        assertThrows(
                EventNotFoundException.class,
                () -> bookEventService.bookEvent(request)
        );

        // validator, equipment repo, save und mapping dürfen in dem Fall ned aufgerufen werden
        verifyNoInteractions(bookingValidator);
        verifyNoInteractions(equipmentRepository);
        verify(bookingRepository, never()).save(any());
        verifyNoInteractions(bookingMapperDTO);
    }

    // 7) bookEvent: event is cancelled → IllegalStateException, kein save(), kein validator
    @Test
    void bookEvent_shouldThrowIllegalStateException_whenEventIsCancelled() {
        // Given: gültige Request, aber Event ist cancelled
        CreateBookingRequest request = createValidRequest();

        Event mockEvent = mock(Event.class);
        when(mockEvent.getCancelled()).thenReturn(true);

        // loadEventForBooking liefert das cancelled Event
        when(bookingRepository.loadEventForBooking(request.getEventId()))
                .thenReturn(mockEvent);

        // When + Then: es soll sofort krachen, bevor irgendwas validiert/gespeichert wird
        assertThrows(
                IllegalStateException.class,
                () -> bookEventService.bookEvent(request)
        );

        // Validator, Equipment-Repo, save und DTO-Mapping dürfen nicht aufgerufen werden
        verifyNoInteractions(bookingValidator);
        verifyNoInteractions(equipmentRepository);
        verify(bookingRepository, never()).save(any());
        verifyNoInteractions(bookingMapperDTO);
    }

    // 8) bookEvent: event ist bereits vorbei → IllegalStateException, kein save
    @Test
    void bookEvent_shouldThrowIllegalStateException_whenEventIsExpired() {
        // Given
        CreateBookingRequest request = createValidRequest();

        Event mockEvent = mock(Event.class);
        when(mockEvent.getCancelled()).thenReturn(false);

        // gestern → Event ist abgelaufen
        when(mockEvent.getDate()).thenReturn(java.time.LocalDate.now().minusDays(1));
        when(mockEvent.getStartTime()).thenReturn(java.time.LocalTime.NOON);

        when(bookingRepository.loadEventForBooking(request.getEventId()))
                .thenReturn(mockEvent);

        // When + Then
        assertThrows(
                IllegalStateException.class,
                () -> bookEventService.bookEvent(request)
        );

        // keine weiteren Aufrufe
        verifyNoInteractions(bookingValidator);
        verifyNoInteractions(equipmentRepository);
        verify(bookingRepository, never()).save(any());
        verifyNoInteractions(bookingMapperDTO);
    }

    // 9) bookEvent: event ist voll → EventFullyBookedException
    @Test
    void bookEvent_shouldThrowEventFullyBookedException_whenNoSeatsRemaining() {
        // Given
        CreateBookingRequest request = createValidRequest();

        Event mockEvent = mock(Event.class);
        when(mockEvent.getCancelled()).thenReturn(false);

        when(mockEvent.getDate()).thenReturn(java.time.LocalDate.now().plusDays(1));
        when(mockEvent.getStartTime()).thenReturn(java.time.LocalTime.NOON);

        when(mockEvent.getId()).thenReturn(42L);

        // Kapazitäten
        when(mockEvent.getMaxParticipants()).thenReturn(10);
        when(mockEvent.getMinParticipants()).thenReturn(0);

        // bereits 10 gebucht → voll
        when(bookingRepository.countOccupiedSeatsForEvent(42L)).thenReturn(10);

        // Event wird geladen
        when(bookingRepository.loadEventForBooking(request.getEventId()))
                .thenReturn(mockEvent);

        // When + Then
        assertThrows(
                EventFullyBookedException.class,
                () -> bookEventService.bookEvent(request)
        );

        // kein Validator, kein Equipment, kein Save
        verifyNoInteractions(bookingValidator);
        verifyNoInteractions(equipmentRepository);
        verify(bookingRepository, never()).save(any());
        verifyNoInteractions(bookingMapperDTO);
    }

    // 10) updateBooking: booking wird upgedatet wenn der input valid ist
    @Test
    void updateBooking_shouldUpdateBookingAndSave_whenValid() {
        // given
        Long bookingId = 1L;
        Booking existingBooking = createBooking(bookingId);

        CreateBookingRequest request = createValidRequest();
        request.setSeats(3);

        Event mockEvent = mock(Event.class);
        when(mockEvent.getCancelled()).thenReturn(false);
        when(mockEvent.getDate()).thenReturn(java.time.LocalDate.now().plusDays(1));
        when(mockEvent.getStartTime()).thenReturn(java.time.LocalTime.NOON);
        when(mockEvent.getId()).thenReturn(42L);
        when(mockEvent.getMaxParticipants()).thenReturn(10);
        when(mockEvent.getMinParticipants()).thenReturn(0);
        when(mockEvent.getPrice()).thenReturn(BigDecimal.valueOf(100));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.loadEventForBooking(42L)).thenReturn(mockEvent);

        // 2 Plätze insgesamt gebucht, alte Buchung hatte 2 Seats → alreadyBookedExcludingThis = 0
        when(bookingRepository.countOccupiedSeatsForEvent(42L)).thenReturn(2);

        // keine Ausrüstung → leere Map
        request.setEquipment(Map.of());

        when(bookingValidator.validate(eq(request), eq(mockEvent), eq(Map.of()), eq(0)))
                .thenReturn(List.of());

        BookingDTO dto = new BookingDTO();
        when(bookingRepository.save(existingBooking)).thenReturn(existingBooking);
        when(bookingMapperDTO.toDTO(existingBooking)).thenReturn(dto);

        existingBooking.setEquipment(new ArrayList<>());
        existingBooking.setParticipants(new ArrayList<>());

        // when
        BookingDTO result = bookEventService.updateBooking(bookingId, request);

        // then
        assertSame(dto, result);
        assertEquals(3, existingBooking.getSeats());

        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, times(2)).countOccupiedSeatsForEvent(42L); // einmal in checkEventCapacityForUpdate, einmal für validate
        verify(bookingRepository).save(existingBooking);
        verify(bookingMapperDTO).toDTO(existingBooking);
    }

    // 11) updateBooking: IllegalStateException wenn das Event cancelled ist
    @Test
    void updateBooking_shouldThrowIllegalState_whenEventCancelled() {
        Long bookingId = 1L;
        Booking existingBooking = createBooking(bookingId);
        CreateBookingRequest request = createValidRequest();

        Event mockEvent = mock(Event.class);
        when(mockEvent.getCancelled()).thenReturn(true); // cancelled

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.loadEventForBooking(request.getEventId())).thenReturn(mockEvent);

        assertThrows(IllegalStateException.class,
                () -> bookEventService.updateBooking(bookingId, request));

        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).loadEventForBooking(request.getEventId());
        verifyNoInteractions(bookingValidator);
        verify(bookingRepository, never()).save(any());
    }

    // 12) updateBooking: BookingValidationException wenn validation fehlerhaft ist - kein save
    @Test
    void updateBooking_shouldThrowBookingValidationException_whenValidatorReturnsErrors() {
        Long bookingId = 1L;
        Booking existingBooking = createBooking(bookingId);
        CreateBookingRequest request = createValidRequest();
        request.setSeats(3);

        Event mockEvent = mock(Event.class);
        when(mockEvent.getCancelled()).thenReturn(false);
        when(mockEvent.getDate()).thenReturn(java.time.LocalDate.now().plusDays(1));
        when(mockEvent.getStartTime()).thenReturn(java.time.LocalTime.NOON);
        when(mockEvent.getId()).thenReturn(42L);
        when(mockEvent.getMaxParticipants()).thenReturn(10);
        when(mockEvent.getMinParticipants()).thenReturn(0);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.loadEventForBooking(42L)).thenReturn(mockEvent);

        when(bookingRepository.countOccupiedSeatsForEvent(42L)).thenReturn(2);

        request.setEquipment(Map.of());

        ValidationError error = new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                "seats",
                "Seats invalid",
                "3"
        );
        when(bookingValidator.validate(eq(request), eq(mockEvent), eq(Map.of()), eq(0)))
                .thenReturn(List.of(error));

        assertThrows(BookingValidationException.class,
                () -> bookEventService.updateBooking(bookingId, request));

        verify(bookingRepository, never()).save(any());
        verify(bookingMapperDTO, never()).toDTO(any());
    }

    // 13) updateBooking: FullyBookedException wenn Event voll und Customer will weiteren Participant adden
    @Test
    void updateBooking_shouldThrowEventFullyBookedException_whenEventHasNoRemainingSeats() {
        Long bookingId = 1L;

        Booking booking = createBooking(bookingId);
        booking.setSeats(2);
        booking.setEventId(42L);
        booking.setEquipment(new ArrayList<>());
        booking.setParticipants(new ArrayList<>());

        CreateBookingRequest request = createValidRequest();
        request.setSeats(3);
        request.setEventId(42L);

        // mock
        Event event = mock(Event.class);
        when(event.getCancelled()).thenReturn(false);
        when(event.getDate()).thenReturn(LocalDate.now().plusDays(1));
        when(event.getStartTime()).thenReturn(LocalTime.NOON);
        when(event.getId()).thenReturn(42L);
        when(event.getMaxParticipants()).thenReturn(10);
        when(event.getMinParticipants()).thenReturn(0);

        // Repository-Verhalten
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.loadEventForBooking(42L)).thenReturn(event);
        when(bookingRepository.countOccupiedSeatsForEvent(42L)).thenReturn(10);

        assertThrows(EventFullyBookedException.class,
                () -> bookEventService.updateBooking(bookingId, request));
    }

}