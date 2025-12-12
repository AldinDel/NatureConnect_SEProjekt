package at.fhv.Event.application.refund;

import at.fhv.Event.application.email.FakeEmailService;
import org.springframework.stereotype.Service;

@Service
public class RefundService {

    private final FakePaymentSystemClient paymentClient;
    private final FakeEmailService emailService;

    public RefundService(FakePaymentSystemClient paymentClient,
                         FakeEmailService emailService) {
        this.paymentClient = paymentClient;
        this.emailService = emailService;
    }

    public void processRefund(String customerEmail, Long bookingId, double amount) {

        // 1) Fake Refund im Payment-System
        paymentClient.refundPayment(bookingId, amount);

        // 2) Fake Email an Kunden
        emailService.sendRefundEmail(customerEmail, bookingId);

        // 3) (Optional) Logging für Prof
        System.out.println("Refund processed for booking " + bookingId);
    }
}
