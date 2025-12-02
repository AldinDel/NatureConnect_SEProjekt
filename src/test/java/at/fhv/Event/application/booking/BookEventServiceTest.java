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
                List.of()          // equipment
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

        // Event wird geladen
        when(bookingRepository.loadEventForBooking(42L)).thenReturn(mockEvent);

        // Seats / Capacity
        when(bookingRepository.countSeatsForEvent(42L)).thenReturn(0);
        when(mockEvent.getMaxParticipants()).thenReturn(10);
        when(mockEvent.getMinParticipants()).thenReturn(0);

        // Equipment leer
        when(equipmentRepository.findByIds(List.of())).thenReturn(Map.of());

        // Validator liefert ECHTEN ValidationError
        ValidationError error = new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                "seats",
                2,
                "Seats invalid"
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
        when(bookingRepository.countSeatsForEvent(42L)).thenReturn(10);

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


}