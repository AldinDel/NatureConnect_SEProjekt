package at.fhv.Event.domain.model.booking;

import java.time.Instant;
import java.util.List;

public class Booking {
    private Long id;
    private Long eventId;
    private String bookerFirstName;
    private String bookerLastName;
    private String bookerEmail;
    private int seats;
    private AudienceType audience;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod; // optional
    private String voucherCode;
    private double discountAmount;
    private double totalPrice;
    private String specialNotes;
    private Instant createdAt;
    private List<BookingParticipant> participants;
    private List<BookingEquipment> equipment;

    public Booking() {}
    public Booking(
            Long eventId,
            String bookerFirstName,
            String bookerLastName,
            String bookerEmail,
            int seats,
            AudienceType audience,
            BookingStatus status,
            PaymentStatus paymentStatus,
            PaymentMethod paymentMethod,
            String voucherCode,
            double discountAmount,
            double totalPrice,
            String specialNotes,
            List<BookingParticipant> participants,
            List<BookingEquipment> equipment
    ) {
        this.eventId = eventId;
        this.bookerFirstName = bookerFirstName;
        this.bookerLastName = bookerLastName;
        this.bookerEmail = bookerEmail;
        this.seats = seats;
        this.audience = audience;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.voucherCode = voucherCode;
        this.discountAmount = discountAmount;
        this.totalPrice = totalPrice;
        this.specialNotes = specialNotes;
        this.createdAt = Instant.now();
        this.participants = participants;
        this.equipment = equipment;
    }

    public void applyVoucher(String code, double discountAmount) {
        this.voucherCode = code;
        this.discountAmount = discountAmount;
        if (this.discountAmount < 0) this.discountAmount = 0;
        recalculateTotal();
    }

    public void recalculateTotal() {
        double basePrice = 0;

        if (equipment != null) {
            basePrice += equipment.stream()
                    .mapToDouble(BookingEquipment::getTotalPrice)
                    .sum();
        }

        this.totalPrice = Math.max(0, basePrice - discountAmount);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getBookerFullName() {
        return bookerFirstName + " " + bookerLastName;
    }

    public boolean isPaid() {
        return paymentStatus == PaymentStatus.PAID;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public List<BookingParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<BookingParticipant> participants) {
        this.participants = participants;
    }

    public List<BookingEquipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<BookingEquipment> equipment) {
        this.equipment = equipment;
    }

}
