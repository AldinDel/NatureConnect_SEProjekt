package at.fhv.Event.domain.model.invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(Long id);

    List<Invoice> findByBookingId(Long bookingId);

    List<Invoice> findAll();

    List<Invoice> findByEventId(Long eventId);

    boolean existsEventPriceForBooking(Long bookingId);
}
