package at.fhv.Event.presentation.rest.request;

import java.util.List;

public class WalkInBookingRequest {

    private String bookerFirstName;
    private String bookerLastName;
    private String email;
    private String paymentMethod;

    private List<WalkInParticipantRequest> participants;
    private List<WalkInEquipmentRequest> equipment;

    public WalkInBookingRequest() {}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<WalkInParticipantRequest> getParticipants() {
        return participants;
    }

    public void setParticipants(List<WalkInParticipantRequest> participants) {
        this.participants = participants;
    }

    public List<WalkInEquipmentRequest> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<WalkInEquipmentRequest> equipment) {
        this.equipment = equipment;
    }
}