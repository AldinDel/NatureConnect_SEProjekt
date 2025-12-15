package at.fhv.Event.domain.model.invoice;

import java.util.Objects;

public class InvoiceId {

    private final Long value;

    private InvoiceId(Long value) {
        this.value = value;
    }

    public static InvoiceId of(Long value) {
        return new InvoiceId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvoiceId)) return false;
        InvoiceId invoiceId = (InvoiceId) o;
        return Objects.equals(value, invoiceId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
