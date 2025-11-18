package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.application.request.booking.BookingRequestMapper;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Service;

@Service
public class BookEventService {

    private final BookingRepository bookingRepository;
    private final BookingRequestMapper bookingRequestMapper;
    private final BookingMapperDTO bookingMapperDTO;

    public BookEventService(
            BookingRepository bookingRepository,
            BookingRequestMapper bookingRequestMapper,
            BookingMapperDTO bookingMapperDTO
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingRequestMapper = bookingRequestMapper;
        this.bookingMapperDTO = bookingMapperDTO;
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    public BookingDTO bookEvent(CreateBookingRequest request) {

        // --- Basic validation ---
        if (request.getSeats() <= 0) {
            throw new IllegalArgumentException("Seats must be > 0.");
        }

        if (request.isGuest() && (request.getEmail() == null || !request.getEmail().contains("@"))) {
            throw new IllegalArgumentException("Guest must provide a valid email.");
        }

        // --- Price Calculation (MVP placeholder) ---
        double unitPrice = 10.0;
        double totalPrice = unitPrice * request.getSeats();

        // --- Request → Domain ---
        Booking booking = bookingRequestMapper.toDomain(request, unitPrice, totalPrice);

        // --- Save ---
        Booking saved = bookingRepository.save(booking);

        // --- Domain → DTO ---
        return bookingMapperDTO.toDTO(saved);
    }
}
