package at.fhv.Event.presentation.rest.response.invoice;

import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceStatus;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceWithEventDTO {

    private final Long id;
    private final Long bookingId;
    private final InvoiceStatus status;
    private final BigDecimal total;
    private final LocalDateTime createdAt;
    private final EventDetailDTO event;
    private final Invoice invoice;

    public InvoiceWithEventDTO(Invoice invoice, EventDetailDTO event) {
        this.invoice = invoice;
        this.event = event;

        this.id = invoice.getId();
        this.bookingId = invoice.getBookingId();
        this.status = invoice.getStatus();
        this.total = invoice.getTotal();
        this.createdAt = invoice.getCreatedAt();
    }

    public Long getId() {
        return id;
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

    public EventDetailDTO getEvent() {
        return event;
    }

    public Invoice getInvoice() {
        return invoice;
    }
}
