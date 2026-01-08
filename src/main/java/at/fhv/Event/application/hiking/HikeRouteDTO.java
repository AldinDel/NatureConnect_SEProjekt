package at.fhv.Event.application.hiking;

import java.util.List;

public class HikeRouteDTO {
    private final String key;
    private final String name;
    private final String difficulty;
    private final Integer lengthKm;
    private final Integer durationMinutes;
    private final List<GetHikeRoutesForEventService.WaypointDTO> waypoints;

    public HikeRouteDTO(
            String key,
            String name,
            String difficulty,
            Integer lengthKm,
            Integer durationMinutes,
            List<GetHikeRoutesForEventService.WaypointDTO> waypoints
    ) {
        this.key = key;
        this.name = name;
        this.difficulty = difficulty;
        this.lengthKm = lengthKm;
        this.durationMinutes = durationMinutes;
        this.waypoints = waypoints;
    }

    public String getKey() { return key; }
    public String getName() { return name; }
    public String getDifficulty() { return difficulty; }
    public Integer getLengthKm() { return lengthKm; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public List<GetHikeRoutesForEventService.WaypointDTO> getWaypoints() { return waypoints; }
}
