package at.fhv.Event.application.request.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CreateEventRequest {
    private String title;
    private String description;
    private String organizer;
    private String category;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String difficulty;
    private Integer minParticipants;
    private Integer maxParticipants;
    private BigDecimal price;
    private String imageUrl;
    private String audience;
    private List<Long> requiredEquipmentIds;
    private List<Long> optionalEquipmentIds;

    public CreateEventRequest() {}

    /* getters and setters */
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public Integer getMinParticipants() { return minParticipants; }
    public void setMinParticipants(Integer minParticipants) { this.minParticipants = minParticipants; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }
    public List<Long> getRequiredEquipmentIds() { return requiredEquipmentIds; }
    public void setRequiredEquipmentIds(List<Long> requiredEquipmentIds) { this.requiredEquipmentIds = requiredEquipmentIds; }
    public List<Long> getOptionalEquipmentIds() { return optionalEquipmentIds; }
    public void setOptionalEquipmentIds(List<Long> optionalEquipmentIds) { this.optionalEquipmentIds = optionalEquipmentIds; }
}
