package at.fhv.Event.infrastructure.persistence.event;

import at.fhv.Event.domain.model.event.Difficulty;
import at.fhv.Event.domain.model.event.EventAudience;
import at.fhv.Event.infrastructure.persistence.equipment.EventEquipmentEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "event", schema="nature_connect")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String organizer;
    private String category;
    private String location;

    @Column(name = "image_url")
    private String imageUrl;


    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private Integer minParticipants;
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 20)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "audience", length = 50)
    private EventAudience audience;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EventEquipmentEntity> eventEquipments = new HashSet<>();

    @Column(name = "is_cancelled", nullable = false)
    private boolean cancelled = false;

    public Boolean getCancelled() {
        return cancelled;
    }
    public void addEquipment(EventEquipmentEntity e) {
        eventEquipments.add(e);
        e.setEvent(this);
    }

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }


    @Column(name = "confirmed_seats", nullable = false)
    private int confirmedSeats = 0;

    @Column(name = "reserved_seats", nullable = false)
    private int reservedSeats = 0;


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "event_hike_route",
            schema = "nature_connect",
            joinColumns = @JoinColumn(name = "event_id")
    )

    @Column(name = "hike_key")
    private Set<String> hikeRouteKeys = new HashSet<>();


    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(Integer minParticipants) {
        this.minParticipants = minParticipants;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public EventAudience getAudience() {
        return audience;
    }

    public void setAudience(EventAudience audience) {
        this.audience = audience;
    }

    public Set<EventEquipmentEntity> getEventEquipments() {
        return eventEquipments;
    }

    public void setEventEquipments(Set<EventEquipmentEntity> eventEquipments) {
        this.eventEquipments = eventEquipments;
    }

    public Set<String> getHikeRouteKeys() {
        return hikeRouteKeys;
    }

    public void setHikeRouteKeys(Set<String> hikeRouteKeys) {
        this.hikeRouteKeys = (hikeRouteKeys == null) ? new HashSet<>() : new HashSet<>(hikeRouteKeys);
    }

}
