package at.fhv.Event.application.request.event;

import at.fhv.Event.domain.model.event.Difficulty;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UpdateEventRequest {
    private String title;
    private String description;
    private String organizer;
    private String category;
    private LocalDate date;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean recurring;
    private LocalDate recurrenceStart;
    private LocalDate recurrenceEnd;
    private Set<DayOfWeek> recurrenceDays;
    private String location;
    private Difficulty difficulty;
    private Integer minParticipants;
    private Integer maxParticipants;
    private BigDecimal price;
    private String imageUrl;
    private String audience;
    private List<EventEquipmentUpdateRequest> equipments = new ArrayList<>();
    private List<String> hikeRouteKeys = new ArrayList<>();

    public UpdateEventRequest() {
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

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public boolean isRecurring() {
        return recurring;
    }

    public LocalDate getRecurrenceStart() {
        return recurrenceStart;
    }

    public LocalDate getRecurrenceEnd() {
        return recurrenceEnd;
    }

    public Set<DayOfWeek> getRecurrenceDays() {
        return recurrenceDays;
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

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public List<EventEquipmentUpdateRequest> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<EventEquipmentUpdateRequest> equipments) {
        this.equipments = equipments;
    }

    public List<String> getHikeRouteKeys() {
        return hikeRouteKeys;
    }

    public void setHikeRouteKeys(List<String> hikeRouteKeys) {
        this.hikeRouteKeys = hikeRouteKeys;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public void setRecurrenceStart(LocalDate recurrenceStart) {
        this.recurrenceStart = recurrenceStart;
    }

    public void setRecurrenceEnd(LocalDate recurrenceEnd) {
        this.recurrenceEnd = recurrenceEnd;
    }

    public void setRecurrenceDays(Set<DayOfWeek> recurrenceDays) {
        this.recurrenceDays = recurrenceDays;
    }

}
