package at.fhv.Event.infrastructure.persistence.hiking;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Hike")
public class HikeNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer eventId;
    private String difficulty;
    private Integer lengthKm;
    private Integer durationMinutes;

    @Relationship(type = "STARTS_AT")
    private WaypointNode start;

    protected HikeNode() {
        // for Neo4j
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getEventId() { return eventId; }
    public String getDifficulty() { return difficulty; }
    public Integer getLengthKm() { return lengthKm; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public WaypointNode getStart() { return start; }
}
