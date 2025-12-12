package at.fhv.Event.application.refund;

import org.springframework.stereotype.Component;

@Component
public class FakePaymentSystemClient {

    public void refundPayment(Long bookingId, double amount) {
        // Simulierter API-Call
        System.out.println("[FAKE PAYMENT] Refund triggered:");
        System.out.println("  Booking ID: " + bookingId);
        System.out.println("  Amount:     " + amount);
    }
}
