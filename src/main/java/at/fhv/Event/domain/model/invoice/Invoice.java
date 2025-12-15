package at.fhv.Event.domain.model.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Invoice {

    private InvoiceId id;
    private Long eventId;
    private Long bookingId;
    private InvoiceStatus status;
    private List<InvoiceLine> lines;
    private BigDecimal total;
    private LocalDateTime createdAt;

    private Invoice() {}

    public static Invoice createInterim(
            Long eventId,
            Long bookingId,
            List<InvoiceLine> lines
    ) {
        Invoice invoice = new Invoice();
        invoice.eventId = eventId;
        invoice.bookingId = bookingId;
        invoice.status = InvoiceStatus.INTERIM;
        invoice.lines = lines;
        invoice.createdAt = LocalDateTime.now();
        invoice.calculateTotal();
        return invoice;
    }

    public static Invoice rehydrate(
            Long eventId,
            Long bookingId,
            InvoiceStatus status,
            BigDecimal total,
            LocalDateTime createdAt
    ) {
        Invoice invoice = new Invoice();
        invoice.eventId = eventId;
        invoice.bookingId = bookingId;
        invoice.status = status;
        invoice.total = total;
        invoice.createdAt = createdAt;
        invoice.lines = List.of();
        return invoice;
    }

    private void calculateTotal() {
        if (lines == null || lines.isEmpty()) {
            this.total = BigDecimal.ZERO;
            return;
        }

        this.total = lines.stream()
                .map(InvoiceLine::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
}
