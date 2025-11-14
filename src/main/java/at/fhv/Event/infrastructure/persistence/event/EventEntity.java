package at.fhv.Event.infrastructure.persistence.event;

import at.fhv.Event.domain.model.event.Difficulty;
import at.fhv.Event.infrastructure.persistence.equipment.EventEquipmentEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event")
public class EventEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String organizer;
    private String category;
    private String location;
    private String imageUrl;
    private String audience;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private Integer minParticipants;
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private BigDecimal price;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventEquipmentEntity> eventEquipments = new ArrayList<>();

    @Column(name = "is_cancelled")
    private Boolean isCancelled = false;

    public Boolean getCancelled() { return isCancelled; }
    public void setCancelled(Boolean cancelled) { this.isCancelled = cancelled; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Integer getMinParticipants() { return minParticipants; }
    public void setMinParticipants(Integer minParticipants) { this.minParticipants = minParticipants; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public List<EventEquipmentEntity> getEventEquipments() { return eventEquipments; }
    public void setEventEquipments(List<EventEquipmentEntity> eventEquipments) { this.eventEquipments = eventEquipments; }
}
