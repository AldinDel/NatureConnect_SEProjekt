package at.fhv.Event.domain.model.event;

import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.exception.EventAlreadyCancelledException;
import at.fhv.Event.domain.model.exception.EventFullyBookedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    private Set<EventEquipment> eventEquipments;
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
                 Set<EventEquipment> eventEquipments,
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
        this.eventEquipments = (eventEquipments == null) ? new HashSet<>() : new HashSet<>(eventEquipments);
        this.hikeRouteKeys = (hikeRouteKeys == null)
                ? new ArrayList<>()
                : new ArrayList<>(hikeRouteKeys);
        this.cancelled = false;
        this.audience = audience;
    }

    public void cancel() {
        if (this.cancelled) {
            throw new EventAlreadyCancelledException(id);
        }
        this.cancelled = true;
    }

    public void updateDetails(String title, String description, BigDecimal price) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        this.title = title.trim();
        this.description = description.trim();
        this.price = price;
    }

    public boolean isBookable(int currentBookedSeats) {
        if (Boolean.TRUE.equals(this.cancelled)) {
            return false;
        }

        if (this.date != null && this.startTime != null) {
            LocalDateTime eventStart = LocalDateTime.of(this.date, this.startTime);
            if (eventStart.isBefore(LocalDateTime.now())) {
                return false;
            }
        }

        if (currentBookedSeats >= this.maxParticipants) {
            return false;
        }
        return true;
    }

    public void validateAvailability() {
        if (Boolean.TRUE.equals(this.cancelled)) {
            throw new IllegalStateException("This event is cancelled and cannot be booked.");
        }

        if (this.date != null && this.startTime != null) {
            LocalDateTime eventStart = LocalDateTime.of(this.date, this.startTime);
            if (eventStart.isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("This event is expired and cannot be booked.");
            }
        }
    }

    public int getAvailableSeats(int currentlyBooked) {
        int min = this.minParticipants != null ? this.minParticipants : 0;
        int capacity = this.maxParticipants - min;
        int remaining = capacity - currentlyBooked;
        return Math.max(0, remaining);
    }

    public boolean hasEnoughSeats(int requestedSeats, int currentBookedSeats) {
        int available = getAvailableSeats(currentBookedSeats);
        return requestedSeats <= available;
    }

    public void validateCapacity(int requestedSeats, int currentlyBooked) {
        int available = getAvailableSeats(currentlyBooked);

        if (available <= 0) {
            throw new EventFullyBookedException(this.id, requestedSeats, 0);
        }

        if (requestedSeats > available) {
            throw new EventFullyBookedException(this.id, requestedSeats, available);
        }
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

    public Set<EventEquipment> getEventEquipments() {
        return eventEquipments;
    }

    public void setEventEquipments(Set<EventEquipment> eventEquipments) {
        this.eventEquipments = (eventEquipments == null)
                ? new HashSet<>()
                : new HashSet<>(eventEquipments);
    }

    public List<String> getHikeRouteKeys() {
        return hikeRouteKeys;
    }

    public void setHikeRouteKeys(List<String> hikeRouteKeys) {
        this.hikeRouteKeys = (hikeRouteKeys == null)
                ? new ArrayList<>()
                : new ArrayList<>(hikeRouteKeys);
    }


}
