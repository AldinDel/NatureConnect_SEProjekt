package at.fhv.Event.presentation.rest.webhook;

public record PaymentWebhookDTO(
        Long bookingId,
        String token,
        String status
) {
}
