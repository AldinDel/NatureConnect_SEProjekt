package at.fhv.Event.domain.model.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
            InvoiceId id,
            Long eventId,
            Long bookingId,
            InvoiceStatus status,
            BigDecimal total,
            LocalDateTime createdAt,
            List<InvoiceLine> lines
    ) {
        Invoice invoice = new Invoice();
        invoice.id = id;
        invoice.eventId = eventId;
        invoice.bookingId = bookingId;
        invoice.status = status;
        invoice.total = total;
        invoice.createdAt = createdAt;
        invoice.lines = lines;
        return invoice;
    }

    public void calculateTotal() {
        if (this.lines == null || this.lines.isEmpty()) {
            this.total = BigDecimal.ZERO;
            return;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (InvoiceLine line : lines) {
            sum = sum.add(line.getTotal());
        }
        this.total = sum;
    }

    public void addLine(InvoiceLine line) {
        if (this.status == InvoiceStatus.FINAL) {
            throw new IllegalStateException("Final invoice cannot be changed");
        }

        if (this.lines == null) {
            this.lines = new ArrayList<>();
        }

        this.lines.add(line);
        calculateTotal();
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

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public Long getId() {
        return id != null ? id.getValue() : null;
    }
}
