package at.fhv.Event.infrastructure.persistence.equipment;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EventEquipmentRepository;
import at.fhv.Event.infrastructure.mapper.EquipmentMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventEquipmentRepositoryImpl implements EventEquipmentRepository {

    private final EventEquipmentJpaRepository jpaRepo;
    private final EquipmentMapper mapper;

    public EventEquipmentRepositoryImpl(EventEquipmentJpaRepository jpaRepo,
                                        EquipmentMapper mapper) {
        this.jpaRepo = jpaRepo;
        this.mapper = mapper;
    }

    @Override
    public List<Equipment> findEquipmentByEventId(Long eventId) {
        return jpaRepo.findByEventId(eventId)
                .stream()
                .map(e -> mapper.toDomain(e.getEquipment()))
                .toList();
    }
}
