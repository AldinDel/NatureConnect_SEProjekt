package at.fhv.Event.application.refund;

import at.fhv.Event.application.email.FakeEmailService;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.event.Event;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class RefundService {

    private final FakePaymentSystemClient paymentClient;
    private final FakeEmailService emailService;

    public RefundService(FakePaymentSystemClient paymentClient,
                         FakeEmailService emailService) {
        this.paymentClient = paymentClient;
        this.emailService = emailService;
    }

    public void processRefund(String customerEmail, Long bookingId, BigDecimal amount) {

        // 1) Fake Refund im Payment-System
        paymentClient.refundPayment(bookingId, amount.doubleValue());

        // 2) Fake Email an Kunden
        emailService.sendRefundEmail(customerEmail, bookingId);

        // 3) (Optional) Logging für Prof
        System.out.println("Refund processed for booking " + bookingId);
    }

    public BigDecimal calculateRefund(Booking booking, Event event) {
        if (event.getDate() == null || event.getStartTime() == null) {
            return BigDecimal.ZERO;
        }

        if (event.getDate() == null || event.getStartTime() == null) {
            return BigDecimal.valueOf(booking.getTotalPrice());
        }

        LocalDateTime eventStart = LocalDateTime.of(event.getDate(), event.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        long daysUntilEvent = ChronoUnit.DAYS.between(now, eventStart);

        BigDecimal price = BigDecimal.valueOf(booking.getTotalPrice());
        BigDecimal refund;

        if (daysUntilEvent >= 28) {
            refund = price;
        }
        else if (daysUntilEvent >= 14) {
            refund = price.multiply(new BigDecimal("0.75"));
        }
        else if (daysUntilEvent >= 3) {
            refund = price.multiply(new BigDecimal("0.30"));
        }
        else {
            refund = BigDecimal.ZERO;
        }

        return refund.setScale(2, RoundingMode.HALF_UP);
    }
}
