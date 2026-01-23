package at.fhv.Event.application.invoice;

import at.fhv.Event.domain.model.invoice.Invoice;

public interface GenerateInvoicePdfService {
    byte[] generate(Invoice invoice);
}
