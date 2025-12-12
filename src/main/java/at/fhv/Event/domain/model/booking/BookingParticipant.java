package at.fhv.Event.domain.model.booking;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_participant")
public class BookingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private int age;

    @Column(name = "booking_id")
    private Long bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_status")
    private ParticipantStatus checkInStatus;

    public BookingParticipant(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.checkInStatus = ParticipantStatus.REGISTERED;
    }


    public BookingParticipant() {}

    public BookingParticipant(String firstName, String lastName, int age, Long bookingId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.bookingId = bookingId;
        this.checkInStatus = ParticipantStatus.REGISTERED;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdult() {
        return age >= 18;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public ParticipantStatus getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(ParticipantStatus checkInStatus) {
        this.checkInStatus = checkInStatus;
    }
}
