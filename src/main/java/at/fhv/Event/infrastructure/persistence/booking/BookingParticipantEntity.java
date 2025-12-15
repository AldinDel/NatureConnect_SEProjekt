package at.fhv.Event.infrastructure.persistence.booking;

import at.fhv.Event.domain.model.booking.ParticipantStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_participant", schema = "nature_connect")
public class BookingParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingEntity booking;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "age", nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_status", nullable = false)
    private ParticipantStatus checkInStatus = ParticipantStatus.REGISTERED;

    public Long getId() {
        return id;
    }

    public BookingEntity getBooking() {
        return booking;
    }

    public void setBooking(BookingEntity booking) {
        this.booking = booking;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setCheckInStatus(ParticipantStatus checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public ParticipantStatus getCheckInStatus() {
        return checkInStatus;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "check_out_status")
    private ParticipantStatus checkOutStatus;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    public ParticipantStatus getCheckOutStatus() {
        return checkOutStatus;
    }

    public void setCheckOutStatus(ParticipantStatus checkOutStatus) {
        this.checkOutStatus = checkOutStatus;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }


}
