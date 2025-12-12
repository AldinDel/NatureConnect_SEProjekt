package at.fhv.Event.application.hiking;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;


@Service
@Profile("!test")
public class GetHikeRoutesForEventService {

    private final Neo4jClient neo4jClient;

    public GetHikeRoutesForEventService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<HikeRouteDTO> getRoutesForEvent(Integer eventId) {

        // 1) Find all hikes for the event
        String hikeQuery = """
                MATCH (h:Hike {eventId: $eventId})
                RETURN h.name AS name,
                       h.difficulty AS difficulty,
                       h.lengthKm AS lengthKm,
                       h.durationMinutes AS durationMinutes,
                       id(h) AS hid
                """;

        List<HikeMeta> hikes = new ArrayList<>(
                neo4jClient.query(hikeQuery)
                        .bind(eventId).to("eventId")
                        .fetchAs(HikeMeta.class)
                        .mappedBy((ts, r) -> new HikeMeta(
                                r.get("hid").asLong(),
                                r.get("name").asString(),
                                r.get("difficulty").isNull() ? null : r.get("difficulty").asString(),
                                r.get("lengthKm").isNull() ? null : r.get("lengthKm").asInt(),
                                r.get("durationMinutes").isNull() ? null : r.get("durationMinutes").asInt()
                        ))
                        .all()
        );


        // 2) For each hike load its waypoints
        List<HikeRouteDTO> result = new ArrayList<>();

        for (HikeMeta meta : hikes) {
            String wpQuery = """
                    MATCH (h:Hike) WHERE id(h) = $hid
                    MATCH (h)-[:STARTS_AT]->(start:Waypoint)
                    OPTIONAL MATCH (start)-[:NEXT*0..]->(wp:Waypoint)
                    WITH collect(DISTINCT start) + collect(DISTINCT wp) AS wps
                    UNWIND wps AS wp
                    WITH DISTINCT wp
                    RETURN wp.name AS name,
                           wp.sequence AS sequence,
                           wp.altitude AS altitude
                    ORDER BY sequence
                    """;


            List<WaypointDto> waypoints = new ArrayList<>(
                    neo4jClient.query(wpQuery)
                            .bind(meta.hid).to("hid")
                            .fetchAs(WaypointDto.class)
                            .mappedBy((ts, r) -> new WaypointDto(
                                    r.get("name").asString(),
                                    r.get("sequence").asInt(),
                                    r.get("altitude").isNull() ? null : r.get("altitude").asInt()
                            ))
                            .all()
            );

            result.add(new HikeRouteDTO(
                    meta.name,
                    meta.difficulty,
                    meta.lengthKm,
                    meta.durationMinutes,
                    waypoints
            ));
        }

        return result;
    }

    public HikeRouteDTO getBestRouteForEvent(Integer eventId, String filter) {

        List<HikeRouteDTO> routes = getRoutesForEvent(eventId);

        if (routes == null || routes.isEmpty()) {
            return null;
        }

        String f = filter == null ? "" : filter.toLowerCase();

        return switch (f) {

            case "shortest" -> routes.stream()
                    .filter(r -> r.getLengthKm() != null)
                    .min((a, b) -> Integer.compare(a.getLengthKm(), b.getLengthKm()))
                    .orElse(routes.get(0));

            case "longest" -> routes.stream()
                    .filter(r -> r.getLengthKm() != null)
                    .max((a, b) -> Integer.compare(a.getLengthKm(), b.getLengthKm()))
                    .orElse(routes.get(0));

            case "easiest" -> routes.stream()
                    .min((a, b) -> Integer.compare(
                            difficultyRank(a.getDifficulty()),
                            difficultyRank(b.getDifficulty())
                    ))
                    .orElse(routes.get(0));

            case "hardest" -> routes.stream()
                    .max((a, b) -> Integer.compare(
                            difficultyRank(a.getDifficulty()),
                            difficultyRank(b.getDifficulty())
                    ))
                    .orElse(routes.get(0));

            default -> routes.get(0);
        };
    }

    private int difficultyRank(String difficulty) {
        if (difficulty == null) return 99;

        return switch (difficulty.toUpperCase()) {
            case "EASY" -> 1;
            case "INTERMEDIATE" -> 2;
            case "HARD" -> 3;
            default -> 99;
        };
    }

    private static class HikeMeta {
        long hid;
        String name;
        String difficulty;
        Integer lengthKm;
        Integer durationMinutes;

        public HikeMeta(long hid, String name, String difficulty,
                        Integer lengthKm, Integer durationMinutes) {
            this.hid = hid;
            this.name = name;
            this.difficulty = difficulty;
            this.lengthKm = lengthKm;
            this.durationMinutes = durationMinutes;
        }
    }

    public static class WaypointDto {
        private final String name;
        private final int sequence;
        private final Integer altitude;

        public WaypointDto(String name, int sequence, Integer altitude) {
            this.name = name;
            this.sequence = sequence;
            this.altitude = altitude;
        }

        public String getName() {
            return name;
        }

        public int getSequence() {
            return sequence;
        }

        public Integer getAltitude() {
            return altitude;
        }
    }
}
