package at.fhv.Event.domain.model.booking;

import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Access(AccessType.FIELD)
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Beziehung zu EventEntity (NICHT zu Domain Event!)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    private String customerName;

    private int numberOfParticipants;

    private LocalDate date;

    public Booking() {}

    public Booking(EventEntity event,
                   String customerName,
                   int numberOfParticipants,
                   LocalDate date) {
        this.event = event;
        this.customerName = customerName;
        this.numberOfParticipants = numberOfParticipants;
        this.date = date;
    }

    public Long getId() { return id; }

    public EventEntity getEvent() { return event; }

    public String getCustomerName() { return customerName; }

    public int getNumberOfParticipants() { return numberOfParticipants; }

    public LocalDate getDate() { return date; }
}
