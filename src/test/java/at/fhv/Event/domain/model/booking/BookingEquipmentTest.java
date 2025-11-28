package at.fhv.Event.domain.model.booking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// 1) totalPrice = quantity * unitPrice
// 2) quantity = 0 → totalPrice = 0
// 3) constructor rejects negative quantity
// 4) constructor rejects negative unitPrice
// 5) constructor rejects null equipmentId


class BookingEquipmentTest {

    @Test
    void getTotalPrice_shouldBeQuantityTimesUnitPrice() {

        BookingEquipment equipment = new BookingEquipment(
                1L,
                3,
                10.0
        );

        double total = equipment.getTotalPrice();

        assertEquals(30.0, total);
    }

    @Test
    void totalPrice_shouldBeZero_whenQuantityIsZero() {
        BookingEquipment eq = new BookingEquipment(1L, 0, 10.0);

        double total = eq.getTotalPrice();

        assertEquals(0.0, total);
    }

    @Test
    void constructor_shouldThrowException_whenQuantityIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new BookingEquipment(1L, -1, 10.0)
        );
    }

    @Test
    void constructor_shouldThrowException_whenUnitPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new BookingEquipment(1L, 2, -5.0)
        );
    }

    @Test
    void constructor_shouldThrowException_whenEquipmentIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new BookingEquipment(null, 2, 10.0)
        );
    }


}
