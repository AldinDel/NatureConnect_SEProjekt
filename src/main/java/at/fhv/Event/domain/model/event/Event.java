package at.fhv.Event.domain.model.event;

import at.fhv.Event.config.PostgreSQLEnumType;
import at.fhv.Event.domain.model.equipment.EventEquipment;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event", schema = "nature_connect")
@Access(AccessType.FIELD)
public class Event {

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventEquipment> eventEquipments = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;


    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    @Type(PostgreSQLEnumType.class)  // ← DIESE ZEILE HINZUFÜGEN
    private Difficulty difficulty;

    @Column(name = "min_participants", nullable = false)
    private Integer minParticipants = 1;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(length = 120)
    private String category;

    @Column(length = 200)
    private String organizer;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 255)
    private String audience;

    public Event() {}

    public Event(String title, String description, LocalDate date, String location, BigDecimal price, String audience) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.price = price;
        this.audience = audience;
    }

    // --- Getter & Setter (bleiben gleich) ---
    public List<EventEquipment> getEventEquipments() {
        return eventEquipments;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public Integer getMinParticipants() { return minParticipants; }
    public void setMinParticipants(Integer minParticipants) { this.minParticipants = minParticipants; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }
}