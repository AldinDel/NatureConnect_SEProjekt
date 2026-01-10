package at.fhv.Event.domain.model.booking;

import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.domain.model.user.CustomerProfile;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


// 1) recalculateTotal(): sums equipment prices and subtracts discount
// 2) applyVoucher(): sets voucher code, applies discount and recalculates total
// 3) applyVoucher(): treats negative discount as zero
// 4) recalculateTotal(): does not allow totalPrice to go below zero
// 5) recalculateTotal(): returns zero when no equipment is present
// 6) confirm(): sets booking status to CONFIRMED
// 7) cancel(): sets booking status to CANCELLED
// 8) markAsPaid(): sets payment status to PAID when fully paid
// 9) markAsBillingReady(): throws if booking is not CONFIRMED
// 10) markAsBillingReady(): sets booking as billing ready when CONFIRMED
// 11) markAsPaid(): sets payment status to PARTIALLY_PAID when not fully paid
// 12) addPayment(): throws if payment amount is negative
// 13) addPayment(): enables PARTIALLY_PAID when payment is below total
// 14) addPayment(): allows zero amount
// 15) confirm(): throws if booking is CANCELLED
// 16) cancel(): sets booking status to CANCELLED even when CONFIRMED
// 17) confirm(): throws if booking is already CONFIRMED
// 18) cancel(): throws if booking is already CANCELLED
// 19) getRemainingAmount(): returns remaining amount after payment
// 20) getRemainingAmount(): returns 0 when fully paid
// 21) makePartialPayment(): throws if payment amount exceeds remaining balance
// 22) prefillFromCustomer(): copies customer data and creates one participant
// 23) makePartialPayment(): reduces remaining amount when a valid partial payment is made
// 24) payFiftyPercent(): pays exactly half of the total price
// 25) payEquipmentItems(List<Long> equipmentIds): pays only selected equipment items
// 26) isFullyPaid(): returns true when remaining amount is 0
// 27) isPartiallyPaid(): returns true when partially paid
// 28) isPaid(): returns true when booking is fully paid
// 29) isCancelled(): returns true when booking is cancelled



class BookingTest {

    //hepler method to create booking with equipment and discount
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
        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 2 * 10 = 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 1 * 5 = 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                5.0
        );

        booking.recalculateTotal();

        assertEquals(20.0, booking.getTotalPrice());
    }

    @Test
    void applyVoucher_shouldSetVoucherCodeAndApplyDiscount() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
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

        BookingEquipment eq = new BookingEquipment(null, 1L, 1, BigDecimal.valueOf(20.0));
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

        BookingEquipment eq = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0));
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

    @Test
    void confirm_shouldSetStatusToConfirmed() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        booking.confirm();

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    void cancel_shouldSetStatusToCancelled() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        booking.cancel();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void markAsPaid_shouldSetPaymentStatusToPaid_whenFullyPaid() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal();

        booking.addPayment(booking.getTotalPrice());

        booking.markAsPaid();

        assertEquals(PaymentStatus.PAID, booking.getPaymentStatus());
    }

    @Test
    void markAsBillingReady_shouldThrow_whenNotConfirmed() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        assertThrows(IllegalStateException.class, booking::markAsBillingReady);
    }

    @Test
    void markAsBillingReady_shouldSetBillingReady_whenConfirmed() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        booking.confirm();
        booking.markAsBillingReady();

        assertEquals(true, booking.isBillingReady());
    }


    @Test
    void markAsPaid_shouldSetPaymentStatusToPartiallyPaid_whenNotFullyPaid() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal();

        booking.addPayment(10.0);

        booking.markAsPaid();

        assertEquals(PaymentStatus.PARTIALLY_PAID, booking.getPaymentStatus());
    }

    @Test
    void addPayment_shouldThrow_whenAmountIsNegative() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        assertThrows(IllegalArgumentException.class, () -> booking.addPayment(-1.0));
    }

    @Test
    void addPayment_shouldEnablePartiallyPaid_whenPaymentIsBelowTotal() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal();

        booking.addPayment(1.0);

        booking.markAsPaid();

        assertEquals(PaymentStatus.PARTIALLY_PAID, booking.getPaymentStatus());
    }

    @Test
    void addPayment_shouldAllowZeroAmount() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        assertDoesNotThrow(() -> booking.addPayment(0.0));
    }

    @Test
    void confirm_shouldThrow_whenCancelled() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        booking.cancel();

        assertThrows(IllegalStateException.class, booking::confirm);
    }

    @Test
    void cancel_shouldSetStatusToCancelled_whenConfirmed() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        booking.confirm();
        booking.cancel();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void confirm_shouldThrow_whenAlreadyConfirmed() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        booking.confirm();

        assertThrows(IllegalStateException.class, booking::confirm);
    }

    @Test
    void cancel_shouldThrow_whenAlreadyCancelled() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        booking.cancel();

        assertThrows(IllegalStateException.class, booking::cancel);
    }

    @Test
    void getRemainingAmount_shouldReturnCorrectRemainingAmount() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal(); // total = 25
        booking.addPayment(10.0);   // paid = 10

        assertEquals(15.0, booking.getRemainingAmount());
    }

    @Test
    void getRemainingAmount_shouldReturnZero_whenFullyPaid() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal();              // total = 25
        booking.addPayment(booking.getTotalPrice()); // fully paid

        assertEquals(0.0, booking.getRemainingAmount());
    }

    @Test
    void makePartialPayment_shouldThrow_whenAmountExceedsRemaining() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal(); // total = 25
        booking.addPayment(10.0);   // remaining = 15

        assertThrows(IllegalArgumentException.class, () -> booking.makePartialPayment(20.0));
    }

    @Test
    void prefillFromCustomer_shouldCopyCustomerDataAndCreateParticipant() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        CustomerProfile customer = new CustomerProfile();
        customer.setFirstName("Anna");
        customer.setLastName("Muster");
        customer.setEmail("anna@example.com");

        booking.prefillFromCustomer(customer);

        assertEquals("Anna", booking.getBookerFirstName());
        assertEquals("Muster", booking.getBookerLastName());
        assertEquals("anna@example.com", booking.getBookerEmail());

        assertEquals(1, booking.getSeats());
        assertEquals(1, booking.getParticipants().size());
        assertEquals("Anna", booking.getParticipants().get(0).getFirstName());
        assertEquals("Muster", booking.getParticipants().get(0).getLastName());
    }

    @Test
    void makePartialPayment_shouldReduceRemainingAmount_whenValidAmount() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal(); // total = 25

        booking.makePartialPayment(10.0);

        assertEquals(15.0, booking.getRemainingAmount());
    }

    @Test
    void payFiftyPercent_shouldPayHalfOfTotalPrice() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal(); // total = 25

        booking.payFiftyPercent();

        assertEquals(12.5, booking.getRemainingAmount());
    }

    @Test
    void payEquipmentItems_shouldPayOnlySelectedEquipmentItems() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // id 1L => 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // id 2L => 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal(); // total = 25

        booking.payEquipmentItems(List.of(1L));

        assertEquals(5.0, booking.getRemainingAmount());
    }

    @Test
    void isFullyPaid_shouldReturnTrue_whenFullyPaid() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal(); // total = 25
        booking.addPayment(booking.getTotalPrice());
        booking.markAsPaid();

        assertEquals(true, booking.isFullyPaid());
    }

    @Test
    void isPartiallyPaid_shouldReturnTrue_whenPartiallyPaid() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal(); // total = 25
        booking.addPayment(10.0);
        booking.markAsPaid();

        assertEquals(true, booking.isPartiallyPaid());
    }

    @Test
    void isPaid_shouldReturnTrue_whenFullyPaid() {

        BookingEquipment eq1 = new BookingEquipment(null, 1L, 2, BigDecimal.valueOf(10.0)); // 20
        BookingEquipment eq2 = new BookingEquipment(null, 2L, 1, BigDecimal.valueOf(5.0));  // 5

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(eq1, eq2),
                0.0
        );

        booking.recalculateTotal(); // total = 25
        booking.addPayment(booking.getTotalPrice());
        booking.markAsPaid();

        assertEquals(true, booking.isPaid());
    }

    @Test
    void isCancelled_shouldReturnTrue_whenCancelled() {

        Booking booking = createBookingWithEquipmentAndDiscount(
                List.of(),
                0.0
        );

        booking.cancel();

        assertEquals(true, booking.isCancelled());
    }

}
