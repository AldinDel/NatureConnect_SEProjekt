package at.fhv.Event.domain.model.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Invoice {

    private InvoiceId id;
    private Long bookingId;
    private InvoiceStatus status;
    private List<InvoiceLine> lines;
    private BigDecimal total;
    private LocalDateTime createdAt;

    public static Invoice createInterim(
            Long bookingId,
            List<InvoiceLine> lines
    ) {
        Invoice invoice = new Invoice();
        invoice.bookingId = bookingId;
        invoice.status = InvoiceStatus.INTERIM;
        invoice.lines = lines;
        invoice.calculateTotal();
        invoice.createdAt = LocalDateTime.now();
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
}
