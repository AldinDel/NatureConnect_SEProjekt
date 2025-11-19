package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.BookingRequestMapper;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.booking.PaymentMethod;
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

    public BookingDTO bookEvent(CreateBookingRequest request) {

        if (request.getSeats() <= 0) {
            throw new IllegalArgumentException("Seats must be > 0.");
        }

        if (request.getBookerEmail() == null || !request.getBookerEmail().contains("@")) {
            throw new IllegalArgumentException("A valid email is required.");
        }

        double basePrice = request.getSeats() * 10.0;

        Booking booking = bookingRequestMapper.toDomain(request, basePrice);

        Booking saved = bookingRepository.save(booking);

        return bookingMapperDTO.toDTO(saved);
    }

    public BookingDTO getDTOById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        return bookingMapperDTO.toDTO(booking);
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }


    public BookingDTO updatePaymentMethod(Long bookingId, String paymentMethod) {
        Booking booking = getById(bookingId);
        booking.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        Booking saved = bookingRepository.save(booking);
        return bookingMapperDTO.toDTO(saved);
    }


}
