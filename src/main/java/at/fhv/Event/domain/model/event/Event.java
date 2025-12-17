package at.fhv.Event.domain.model.event;

import at.fhv.Event.domain.model.equipment.EventEquipment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Event {

    private Long id;
    private String title;
    private String description;
    private String organizer;
    private String category;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private Difficulty difficulty;
    private Integer minParticipants;
    private Integer maxParticipants;
    private BigDecimal price;
    private String imageUrl;
    private EventAudience audience;
    private Boolean cancelled = false;
    private List<EventEquipment> eventEquipments;
    private List<String> hikeRouteKeys;


    public Event(Long id,
                 String title,
                 String description,
                 String organizer,
                 String category,
                 LocalDate date,
                 LocalTime startTime,
                 LocalTime endTime,
                 String location,
                 Difficulty difficulty,
                 Integer minParticipants,
                 Integer maxParticipants,
                 BigDecimal price,
                 String imageUrl,
                 EventAudience audience,
                 List<EventEquipment> eventEquipments,
                 List<String> hikeRouteKeys) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.organizer = organizer;
        this.category = category;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.difficulty = difficulty;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.imageUrl = imageUrl;
        this.eventEquipments = eventEquipments;
        this.hikeRouteKeys = (hikeRouteKeys == null) ? List.of() : hikeRouteKeys;
        this.cancelled = false;
        this.audience = audience;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public EventAudience getAudience() {
        return audience;
    }

    public void setAudience(EventAudience audience) {
        this.audience = audience;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public List<EventEquipment> getEventEquipments() {
        return eventEquipments;
    }

    public void setEventEquipments(List<EventEquipment> eventEquipments) {
        this.eventEquipments = eventEquipments;
    }

    public List<String> getHikeRouteKeys() {
        return hikeRouteKeys;
    }

    public void setHikeRouteKeys(List<String> hikeRouteKeys) {
        this.hikeRouteKeys = (hikeRouteKeys == null) ? List.of() : hikeRouteKeys;
    }

}
