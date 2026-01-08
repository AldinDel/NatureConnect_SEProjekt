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
        String optimizedQuery = """
            MATCH (h:Hike)
            OPTIONAL MATCH (h)-[:STARTS_AT]->(start:Waypoint)
            OPTIONAL MATCH (start)-[:NEXT*0..]->(wp:Waypoint)
            WITH h, COLLECT(DISTINCT wp) AS waypoints
            RETURN h.key AS key,
                   h.name AS name,
                   h.difficulty AS difficulty,
                   h.lengthKm AS lengthKm,
                   h.durationMinutes AS durationMinutes,
                   waypoints
            ORDER BY h.lengthKm ASC
            """;

        Collection<HikeRouteDTO> routes = neo4jClient.query(optimizedQuery)
                .fetchAs(HikeRouteDTO.class)
                .mappedBy((ts, record) -> {
                    List<WaypointDTO> waypointDTOs = new ArrayList<>();
                    record.get("waypoints").asList(value -> {
                        if (value instanceof org.neo4j.driver.types.Node node) {
                            if (node.containsKey("name") && node.containsKey("sequence")) {
                                waypointDTOs.add(new WaypointDTO(
                                        node.get("name").asString(),
                                        node.get("sequence").asInt()
                                ));
                            }
                        }
                        return null;
                    });

                    waypointDTOs.sort(Comparator.comparingInt(WaypointDTO::getSequence));

                    return new HikeRouteDTO(
                            record.get("key").isNull() ? null : record.get("key").asString(),
                            record.get("name").asString(),
                            record.get("difficulty").isNull() ? null : record.get("difficulty").asString(),
                            record.get("lengthKm").isNull() ? null : record.get("lengthKm").asInt(),
                            record.get("durationMinutes").isNull() ? null : record.get("durationMinutes").asInt(),
                            waypointDTOs
                    );
                }).all();
        return new ArrayList<>(routes);
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
