package at.fhv.Event.presentation.rest.response.booking;

import at.fhv.Event.domain.model.booking.ParticipantCheckInStatus;

public class ParticipantDTO {

    private Long participantId;
    private Long bookingId;
    private String bookerName;
    private String participantName;
    private int participantAge;
    private String bookingStatus;
    private String paymentStatus;
    private ParticipantCheckInStatus checkInStatus;
    private boolean checkedOut;

    public ParticipantDTO(
            Long participantId,
            Long bookingId,
            String bookerName,
            String participantName,
            int participantAge,
            String bookingStatus,
            String paymentStatus,
            ParticipantCheckInStatus checkInStatus,
            boolean checkedOut
    ) {
        this.participantId = participantId;
        this.bookingId = bookingId;
        this.bookerName = bookerName;
        this.participantName = participantName;
        this.participantAge = participantAge;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.checkInStatus = checkInStatus;
        this.checkedOut = checkedOut;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getBookerName() {
        return bookerName;
    }

    public String getParticipantName() {
        return participantName;
    }

    public int getParticipantAge() {
        return participantAge;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public ParticipantCheckInStatus getCheckInStatus() {
        return checkInStatus;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }
}
