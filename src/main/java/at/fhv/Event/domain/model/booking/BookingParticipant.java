package at.fhv.Event.domain.model.booking;

import java.time.LocalDateTime;

public class BookingParticipant {

    private Long id;
    private Long bookingId;
    private String firstName;
    private String lastName;
    private Integer age;
    private ParticipantCheckInStatus checkInStatus;
    private ParticipantCheckOutStatus checkOutStatus;
    private LocalDateTime checkOutTime;

    public BookingParticipant(
            Long id,
            Long bookingId,
            String firstName,
            String lastName,
            Integer age,
            ParticipantCheckInStatus checkInStatus,
            ParticipantCheckOutStatus checkOutStatus,
            LocalDateTime checkOutTime) {
        this.id = id;
        this.bookingId = bookingId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.checkInStatus = checkInStatus;
        this.checkOutStatus = checkOutStatus;
        this.checkOutTime = checkOutTime;
    }

    public Long getId() {
        return id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setCheckOutTime(LocalDateTime time) {
        this.checkOutTime = time;
    }

    public ParticipantCheckInStatus getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(ParticipantCheckInStatus checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public ParticipantCheckOutStatus getCheckOutStatus() {
        return checkOutStatus;
    }

    public void setCheckOutStatus(ParticipantCheckOutStatus checkOutStatus) {
        this.checkOutStatus = checkOutStatus;
    }

    public static BookingParticipant createNew(
            Long bookingId,
            String firstName,
            String lastName,
            Integer age
    ) {
        return new BookingParticipant(
                null,
                bookingId,
                firstName,
                lastName,
                age,
                ParticipantCheckInStatus.REGISTERED,
                ParticipantCheckOutStatus.NOT_CHECKED_OUT,
                null
        );
    }
}
