package at.fhv.Event.domain.model.booking;

import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

// 1) recalculateTotal sums equipment and subtracts discount
// 2) applyVoucher sets code + discount + recalculates
// 3) negative discount → treated as 0
// 4) discount must not make totalPrice negative
// 5) recalculateTotal with no equipment → totalPrice = 0


class BookingTest {

    private Booking createBookingWithEquipmentAndDiscount(
            List<BookingEquipment> equipment,
            double discountAmount
    ) {
        return new Booking(
                42L,
                "Max",
                "Mustermann",
                "max@example.com",
                1,
                AudienceType.INDIVIDUAL,
                BookingStatus.PENDING,
                PaymentStatus.UNPAID,
                PaymentMethod.ON_SITE,
                null,
                discountAmount,
                0.0,
                null,
                List.of(),
                equipment
        );
    }

    @Test
    void recalculateTotal_shouldSumEquipmentAndSubtractDiscount() {
        // Given: zwei Equipments mit 20 + 5 = 25 Euro
        BookingEquipment eq1 = new BookingEquipment(1L, 2, 10.0); // 2 * 10 = 20
        BookingEquipment eq2 = new BookingEquipment(2L, 1, 5.0);  // 1 * 5 = 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                5.0
        );

        booking.recalculateTotal();

        assertEquals(20.0, booking.getTotalPrice());
    }

    @Test
    void applyVoucher_shouldSetVoucherCodeAndApplyDiscount() {

        BookingEquipment eq1 = new BookingEquipment(1L, 2, 10.0); // 20
        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1),
                0.0
        );

        booking.applyVoucher("DISC5", 5.0);

        assertEquals("DISC5", booking.getVoucherCode());
        assertEquals(5.0, booking.getDiscountAmount());

        assertEquals(15.0, booking.getTotalPrice());
    }

    @Test
    void applyVoucher_shouldNotAllowNegativeDiscount() {

        BookingEquipment eq = new BookingEquipment(1L, 1, 20.0);
        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq),
                0.0
        );

        booking.applyVoucher("BAD", -10.0);

        assertEquals("BAD", booking.getVoucherCode());
        assertEquals(0.0, booking.getDiscountAmount());
        assertEquals(20.0, booking.getTotalPrice()); // 20 - 0
    }

    @Test
    void recalculateTotal_shouldNotGoBelowZero() {

        BookingEquipment eq = new BookingEquipment(1L, 2, 10.0);
        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq),
                50.0
        );

        booking.recalculateTotal();

        assertEquals(0.0, booking.getTotalPrice());
    }

    @Test
    void recalculateTotal_shouldBeZeroWithoutEquipment() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        booking.recalculateTotal();

        assertEquals(0.0, booking.getTotalPrice());
    }


}
