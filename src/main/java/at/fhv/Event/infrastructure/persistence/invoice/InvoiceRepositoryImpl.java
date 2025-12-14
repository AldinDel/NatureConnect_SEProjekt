package at.fhv.Event.infrastructure.persistence.invoice;

import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import at.fhv.Event.domain.model.invoice.InvoiceStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InvoiceRepositoryImpl implements InvoiceRepository {

    private final InvoiceJpaRepository jpa;

    public InvoiceRepositoryImpl(InvoiceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceJpaEntity entity = new InvoiceJpaEntity();
        entity.setBookingId(invoice.getBookingId());
        entity.setStatus(invoice.getStatus().name());
        entity.setTotal(invoice.getTotal());
        entity.setCreatedAt(invoice.getCreatedAt());

        InvoiceJpaEntity saved = jpa.save(entity);

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
                .collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findAll() {
        return jpa.findAll()
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private Invoice mapToDomain(InvoiceJpaEntity entity) {
        Invoice invoice = Invoice.createInterim(
                entity.getBookingId(),
                List.of() // Lines kommen später
        );

        invoice.setStatus(InvoiceStatus.valueOf(entity.getStatus()));

        return invoice;
    }
}
