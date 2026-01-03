package at.fhv.Event.domain.model.booking;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// 1) totalPrice = quantity * unitPrice
// 2) constructor rejects quantity zero
// 3) constructor rejects negative quantity
// 4) constructor rejects negative unitPrice
// 5) constructor rejects null equipmentId


class BookingEquipmentTest {

    @Test
    void getTotalPrice_shouldBeQuantityTimesUnitPrice() {

        BookingEquipment equipment = new BookingEquipment(
                null,
                1L,
                3,
                BigDecimal.valueOf(10.0)
        );

        BigDecimal total = equipment.getTotalPrice();
        assertEquals(BigDecimal.valueOf(30.0), total);
    }

    @Test
    void constructor_shouldThrowException_whenQuantityIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BookingEquipment(
                        null,
                        1L,
                        0,      // nicht erlaubt
                        BigDecimal.valueOf(10.0)
                )
        );
    }


    @Test
    void constructor_shouldThrowException_whenQuantityIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new BookingEquipment(null, 1L, -1, BigDecimal.valueOf(10.0))
        );
    }

    @Test
    void constructor_shouldThrowException_whenUnitPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(-5.0))
        );
    }

    @Test
    void constructor_shouldThrowException_whenEquipmentIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new BookingEquipment(null, null, 2, BigDecimal.valueOf(10.0))
        );
    }


}
