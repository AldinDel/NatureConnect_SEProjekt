package at.fhv.Event.application.email;

import org.springframework.stereotype.Service;

@Service
public class FakeEmailService {

    public void sendRefundEmail(String customerEmail, Long bookingId) {
        // Keine echte Email — nur simuliert
        System.out.println("[FAKE EMAIL] Refund email would be sent to "
                + customerEmail + " for booking " + bookingId);
    }
}
