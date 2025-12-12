package at.fhv.Event.infrastructure.persistence.hiking;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Waypoint")
public class WaypointNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer order;
    private Integer altitude;

    @Relationship(type = "NEXT")
    private List<WaypointNode> next;

    protected WaypointNode() {
        // for Neo4j
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getOrder() { return order; }
    public Integer getAltitude() { return altitude; }
    public List<WaypointNode> getNext() { return next; }
}
