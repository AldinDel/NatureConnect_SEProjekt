package at.fhv.Event.domain.model.booking;

public class BookingParticipant {

    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private ParticipantStatus checkInStatus;

    public BookingParticipant(
            Long id,
            String firstName,
            String lastName,
            int age,
            ParticipantStatus checkInStatus
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.checkInStatus = checkInStatus;
    }


    public Long getId() { return id; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() { return age; }

    public ParticipantStatus getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(ParticipantStatus status) {
        this.checkInStatus = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public static BookingParticipant createNew(
            String firstName,
            String lastName,
            int age
    ) {
        return new BookingParticipant(
                null,
                firstName,
                lastName,
                age,
                ParticipantStatus.REGISTERED
        );
    }

}
