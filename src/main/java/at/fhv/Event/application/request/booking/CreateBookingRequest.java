package at.fhv.Event.application.request.booking;

import at.fhv.Event.domain.model.booking.AudienceType;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.equipment.EquipmentSelection;

import java.util.List;
import java.util.Map;

public class CreateBookingRequest {

    private Long eventId;

    private String bookerFirstName;
    private String bookerLastName;
    private String bookerEmail;

    private int seats;
    private AudienceType audience;

    private String voucherCode;
    private String specialNotes;
    private PaymentMethod paymentMethod;
    private Integer discountPercent;

    private List<ParticipantDTO> participants;
    private Map<Long, EquipmentSelection> equipment;

    public Map<Long, EquipmentSelection> getEquipment() { return equipment; }
    public void setEquipment(Map<Long, EquipmentSelection> equipment) { this.equipment = equipment; }


    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getBookerFirstName() {
        return bookerFirstName;
    }

    public void setBookerFirstName(String bookerFirstName) {
        this.bookerFirstName = bookerFirstName;
    }

    public String getBookerLastName() {
        return bookerLastName;
    }

    public void setBookerLastName(String bookerLastName) {
        this.bookerLastName = bookerLastName;
    }

    public String getBookerEmail() {
        return bookerEmail;
    }

    public void setBookerEmail(String bookerEmail) {
        this.bookerEmail = bookerEmail;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public AudienceType getAudience() {
        return audience;
    }

    public void setAudience(AudienceType audience) {
        this.audience = audience;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }

    public List<ParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }
}
