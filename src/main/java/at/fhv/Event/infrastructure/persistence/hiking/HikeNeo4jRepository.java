package at.fhv.Event.infrastructure.persistence.hiking;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface HikeNeo4jRepository extends Neo4jRepository<HikeNode, Long> {

    Optional<HikeNode> findByEventId(Integer eventId);

}
