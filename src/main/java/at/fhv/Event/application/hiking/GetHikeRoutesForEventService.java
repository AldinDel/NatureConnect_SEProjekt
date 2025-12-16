package at.fhv.Event.application.hiking;

import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Profile("!test")
public class GetHikeRoutesForEventService {

    private final Neo4jClient neo4jClient;

    public GetHikeRoutesForEventService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<HikeRouteDTO> getAllRoutes() {

        String hikeQuery = """
                MATCH (h:Hike)
                RETURN h.key AS key,
                       h.name AS name,
                       h.difficulty AS difficulty,
                       h.lengthKm AS lengthKm,
                       h.durationMinutes AS durationMinutes,
                       id(h) AS hid
                ORDER BY h.lengthKm ASC
                """;

        List<HikeMeta> hikes = new ArrayList<>(
                neo4jClient.query(hikeQuery)
                        .fetchAs(HikeMeta.class)
                        .mappedBy((ts, r) -> new HikeMeta(
                                r.get("hid").asLong(),
                                r.get("key").isNull() ? null : r.get("key").asString(),
                                r.get("name").asString(),
                                r.get("difficulty").isNull() ? null : r.get("difficulty").asString(),
                                r.get("lengthKm").isNull() ? null : r.get("lengthKm").asInt(),
                                r.get("durationMinutes").isNull() ? null : r.get("durationMinutes").asInt()
                        ))
                        .all()
        );

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
                           wp.sequence AS sequence
                    ORDER BY sequence
                    """;

            List<WaypointDTO> waypoints = new ArrayList<>(
                    neo4jClient.query(wpQuery)
                            .bind(meta.hid).to("hid")
                            .fetchAs(WaypointDTO.class)
                            .mappedBy((ts, r) -> new WaypointDTO(
                                    r.get("name").asString(),
                                    r.get("sequence").asInt()
                            ))
                            .all()
            );

            result.add(new HikeRouteDTO(
                    meta.key,
                    meta.name,
                    meta.difficulty,
                    meta.lengthKm,
                    meta.durationMinutes,
                    waypoints
            ));
        }

        return result;
    }

    public HikeRouteDTO getBestRoute(String filter) {

        List<HikeRouteDTO> routes = getAllRoutes();

        if (routes == null || routes.isEmpty()) {
            return null;
        }

        String f = filter == null ? "" : filter.toLowerCase();

        return switch (f) {
            case "shortest" -> routes.stream()
                    .filter(r -> r.getLengthKm() != null)
                    .min(Comparator.comparingInt(HikeRouteDTO::getLengthKm))
                    .orElse(routes.get(0));

            case "longest" -> routes.stream()
                    .filter(r -> r.getLengthKm() != null)
                    .max(Comparator.comparingInt(HikeRouteDTO::getLengthKm))
                    .orElse(routes.get(0));

            case "easiest" -> routes.stream()
                    .min(Comparator.comparingInt(r -> difficultyRank(r.getDifficulty())))
                    .orElse(routes.get(0));

            case "hardest" -> routes.stream()
                    .max(Comparator.comparingInt(r -> difficultyRank(r.getDifficulty())))
                    .orElse(routes.get(0));

            default -> routes.get(0);
        };
    }

    public GraphDTO getGraphForRoute(String key) {

        String query = """
                MATCH (h:Hike {key:$key})-[:STARTS_AT]->(start:Waypoint)
                OPTIONAL MATCH (start)-[:NEXT*0..]->(wp:Waypoint)
                WITH collect(DISTINCT start) + collect(DISTINCT wp) AS wps
                UNWIND wps AS wp
                WITH DISTINCT wp
                OPTIONAL MATCH (wp)-[:NEXT]->(next:Waypoint)
                RETURN wp.id AS fromId,
                       wp.name AS fromName,
                       next.id AS toId,
                       next.name AS toName
                """;

        LinkedHashMap<String, GraphNodeDTO> nodes = new LinkedHashMap<>();
        List<GraphEdgeDTO> edges = new ArrayList<>();

        neo4jClient.query(query)
                .bind(key).to("key")
                .fetch()
                .all()
                .forEach(row -> {
                    String fromId = (String) row.get("fromId");
                    String fromName = (String) row.get("fromName");
                    String toId = (String) row.get("toId");
                    String toName = (String) row.get("toName");

                    if (fromId != null && fromName != null) {
                        nodes.putIfAbsent(fromId, new GraphNodeDTO(fromId, fromName));
                    }
                    if (toId != null && toName != null) {
                        nodes.putIfAbsent(toId, new GraphNodeDTO(toId, toName));
                    }
                    if (fromId != null && toId != null) {
                        edges.add(new GraphEdgeDTO(fromId, toId));
                    }
                });

        return new GraphDTO(new ArrayList<>(nodes.values()), edges);
    }

    private int difficultyRank(String difficulty) {
        if (difficulty == null) return 99;

        return switch (difficulty.toUpperCase()) {
            case "BEGINNER" -> 1;
            case "INTERMEDIATE" -> 2;
            case "ADVANCED" -> 3;
            default -> 99;
        };
    }

    private static class HikeMeta {
        long hid;
        String key;
        String name;
        String difficulty;
        Integer lengthKm;
        Integer durationMinutes;

        public HikeMeta(long hid, String key, String name, String difficulty,
                        Integer lengthKm, Integer durationMinutes) {
            this.hid = hid;
            this.key = key;
            this.name = name;
            this.difficulty = difficulty;
            this.lengthKm = lengthKm;
            this.durationMinutes = durationMinutes;
        }
    }

    public static class WaypointDTO {
        private final String name;
        private final int sequence;

        public WaypointDTO(String name, int sequence) {
            this.name = name;
            this.sequence = sequence;
        }

        public String getName() {
            return name;
        }

        public int getSequence() {
            return sequence;
        }
    }
}
