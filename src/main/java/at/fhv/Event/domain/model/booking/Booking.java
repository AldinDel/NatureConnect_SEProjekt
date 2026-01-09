package at.fhv.Event.domain.model.booking;

import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.domain.model.user.CustomerProfile;

import java.math.BigDecimal;
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
    private PaymentMethod paymentMethod;
    private String voucherCode;
    private double discountAmount;
    private double totalPrice;
    private double paidAmount;
    private String specialNotes;
    private Instant createdAt;
    private List<BookingParticipant> participants;
    private List<BookingEquipment> equipment;
    private boolean billingReady;


    public Booking() {
        this.paidAmount = 0.0;
    }
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
        this.paidAmount = 0.0;
        this.specialNotes = specialNotes;
        this.createdAt = Instant.now();
        this.participants = participants;
        this.equipment = equipment;
    }

    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed.");
        }
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking cancelled.");
        }

        this.status = BookingStatus.CANCELLED;
        this.paymentStatus = PaymentStatus.REFUNDED;
    }

    public void markAsPaid() {
        if (this.paidAmount >= this.totalPrice) {
            this.paymentStatus = PaymentStatus.PAID;
        } else {
            this.paymentStatus = PaymentStatus.PARTIALLY_PAID;
        }
    }

    public void addPayment(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        this.paidAmount += amount;
        markAsPaid();
    }

    public void markAsBillingReady() {
        if (this.status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be billed");
        }
        this.billingReady = true;
    }

    public boolean isFullyPaid() {
        return this.paidAmount >= this.totalPrice;
    }

    public boolean isCancelled() {
        return this.status == BookingStatus.CANCELLED;
    }

    public double getRemainingAmount() {
        double remaining = this.totalPrice - this.paidAmount;
        return Math.max(0, remaining);
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
                    .map(BookingEquipment::getTotalPrice)
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum();
        }

        this.totalPrice = Math.max(0, basePrice - discountAmount);
    }

    public void prefillFromCustomer(CustomerProfile customer) {
        this.bookerFirstName = customer.getFirstName();
        this.bookerLastName  = customer.getLastName();
        this.bookerEmail     = customer.getEmail();

        BookingParticipant p1 = BookingParticipant.createNew(
                this.id,
                customer.getFirstName(),
                customer.getLastName(),
                null
        );

        this.participants = List.of(p1);
        this.seats = 1;
    }

    public void makePartialPayment(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        double remaining = getRemainingAmount();
        if (amount > remaining) {
            throw new IllegalArgumentException("Payment amount exceeds remaining balance");
        }

        this.paidAmount += amount;

        if (this.paidAmount >= this.totalPrice) {
            this.paymentStatus = PaymentStatus.PAID;
        } else if (this.paidAmount > 0) {
            this.paymentStatus = PaymentStatus.PARTIALLY_PAID;
        }
    }

    public void payFiftyPercent() {
        double halfAmount = totalPrice * 0.5;
        double remainingToHalf = halfAmount - paidAmount;

        if (remainingToHalf <= 0) {
            throw new IllegalStateException("50% or more has already been paid");
        }

        makePartialPayment(remainingToHalf);
    }

    public void payEquipmentItems(List<Long> equipmentIds) {
        if (equipment == null || equipmentIds == null) return;

        double equipmentTotal = equipment.stream()
                .filter(e -> equipmentIds.contains(e.getEquipmentId()))
                .map(BookingEquipment::getTotalPrice)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        if (equipmentTotal > 0 && equipmentTotal <= getRemainingAmount()) {
            makePartialPayment(equipmentTotal);
        }
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

    public boolean isPartiallyPaid() {
        return paymentStatus == PaymentStatus.PARTIALLY_PAID;
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

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
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

    public boolean isBillingReady() {
        return billingReady;
    }

    public void setBillingReady(boolean billingReady) {
        this.billingReady = billingReady;
    }


}
