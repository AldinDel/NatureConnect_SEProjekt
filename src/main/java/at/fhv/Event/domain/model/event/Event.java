package at.fhv.Event.domain.model.event;

import at.fhv.Event.domain.model.equipment.EventEquipment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Event {

    private final Long id;
    private final String title;
    private final String description;
    private final String organizer;
    private final String category;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String location;
    private final Difficulty difficulty;
    private final Integer minParticipants;
    private final Integer maxParticipants;
    private final BigDecimal price;
    private final String imageUrl;
    private final String audience;

    private Boolean cancelled = false;
    private List<EventEquipment> eventEquipments;

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
                 String audience,
                 List<EventEquipment> eventEquipments) {

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
        this.cancelled = false;
        this.audience = audience;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getOrganizer() { return organizer; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public Difficulty getDifficulty() { return difficulty; }
    public Integer getMinParticipants() { return minParticipants; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public BigDecimal getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public List<EventEquipment> getEventEquipments() { return eventEquipments; }

    public Boolean getCancelled() { return cancelled; }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Boolean cancelled() {
        return cancelled;
    }

    public String getAudience() {
        return audience;
    }

}
