package at.fhv.Event.infrastructure.persistence.invoice;

import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceId;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import at.fhv.Event.domain.model.invoice.InvoiceStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InvoiceRepositoryImpl implements InvoiceRepository {

    private final InvoiceJpaRepository jpa;
    private final InvoiceItemJpaRepository itemJpa;

    public InvoiceRepositoryImpl(
            InvoiceJpaRepository jpa,
            InvoiceItemJpaRepository itemJpa
    ) {
        this.jpa = jpa;
        this.itemJpa = itemJpa;
    }

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceJpaEntity entity = new InvoiceJpaEntity();
        entity.setEventId(invoice.getEventId());
        entity.setBookingId(invoice.getBookingId());
        entity.setStatus(invoice.getStatus().name());
        entity.setTotal(invoice.getTotal());
        entity.setCreatedAt(invoice.getCreatedAt());

        InvoiceJpaEntity saved = jpa.save(entity);

        invoice.getLines().forEach(line -> {
            InvoiceItemEntity item = new InvoiceItemEntity();
            item.setInvoice(saved);
            item.setEquipmentId(line.getEquipmentId());
            item.setQuantity(line.getQuantity());
            item.setUnitPrice(line.getUnitPrice());
            item.setTotalPrice(line.getTotal());

            itemJpa.save(item);
        });

        return mapToDomain(saved);
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return jpa.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Invoice> findByBookingId(Long bookingId) {
        return jpa.findByBookingId(bookingId)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Invoice> findByEventId(Long eventId) {
        return jpa.findByEventId(eventId)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Invoice> findAll() {
        return jpa.findAll()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    private Invoice mapToDomain(InvoiceJpaEntity entity) {
        return Invoice.rehydrate(
                InvoiceId.of(entity.getId()),
                entity.getEventId(),
                entity.getBookingId(),
                InvoiceStatus.valueOf(entity.getStatus()),
                entity.getTotal(),
                entity.getCreatedAt()
        );
    }

    @Override
    public boolean existsEventPriceForBooking(Long bookingId) {
        return itemJpa.existsByInvoice_BookingIdAndEquipmentIdIsNull(bookingId);
    }

}
