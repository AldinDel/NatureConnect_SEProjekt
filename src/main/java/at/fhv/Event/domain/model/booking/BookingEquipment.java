package at.fhv.Event.domain.model.booking;

import at.fhv.Event.domain.model.exception.BookingValidationException;
import at.fhv.Event.domain.model.exception.ValidationError;
import at.fhv.Event.domain.model.exception.ValidationErrorType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookingEquipment {
    private Long id;
    private Long bookingId;
    private Long equipmentId;
    private int quantity;
    private BigDecimal totalPrice;
    private BigDecimal pricePerUnit;
    private boolean invoiced;

    public BookingEquipment() {
    }

    public BookingEquipment(Long bookingId, Long equipmentId, int quantity, BigDecimal pricePerUnit) {
        List<ValidationError> errors = new ArrayList<>();

        if (equipmentId == null) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "equipmentId",
                    null,
                    "Equipment ID must not be null"
            ));
        }
        if (quantity < 1) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "quantity",
                    quantity,
                    "Quantity must be at least 1"
            ));
        }
        if (pricePerUnit.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "pricePerUnit",
                    pricePerUnit,
                    "Unit price must be greater than or equal to 0"
            ));
        }

        if (!errors.isEmpty()) {
            throw new BookingValidationException(errors);
        }

        this.bookingId = bookingId;
        this.equipmentId = equipmentId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.invoiced = false;
        this.totalPrice = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalPrice() {
        return pricePerUnit.multiply(BigDecimal.valueOf(quantity));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public boolean isInvoiced() {
        return invoiced;
    }

    public void setInvoiced(boolean invoiced) {
        this.invoiced = invoiced;
    }
}
