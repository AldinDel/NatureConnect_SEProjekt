package at.fhv.Event.domain.model.booking;

public class BookingParticipant {

    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private ParticipantCheckInStatus checkInStatus;
    private ParticipantCheckOutStatus checkOutStatus;

    public BookingParticipant(
            Long id,
            String firstName,
            String lastName,
            Integer age,
            ParticipantCheckInStatus checkInStatus,
            ParticipantCheckOutStatus checkOutStatus
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.checkInStatus = checkInStatus;
        this.checkOutStatus = checkOutStatus;
    }

    public Long getId() {
        return id;
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
            String firstName,
            String lastName,
            Integer age
    ) {
        return new BookingParticipant(
                null,
                firstName,
                lastName,
                age,
                ParticipantCheckInStatus.REGISTERED,
                ParticipantCheckOutStatus.NOT_CHECKED_OUT
        );
    }
}
