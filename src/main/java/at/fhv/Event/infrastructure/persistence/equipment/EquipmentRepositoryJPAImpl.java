package at.fhv.Event.infrastructure.persistence.equipment;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.infrastructure.mapper.EquipmentMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EquipmentRepositoryJPAImpl implements EquipmentRepository {

    private final EquipmentJpaRepository jpa;
    private final EquipmentMapper mapper;

    public EquipmentRepositoryJPAImpl(EquipmentJpaRepository jpa, EquipmentMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Equipment> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Equipment> findByNameIgnoreCase(String name) {
        return jpa.findByNameIgnoreCase(name).map(mapper::toDomain);
    }

    @Override
    public List<Equipment> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void save(Equipment equipment) {
        var entity = mapper.toEntity(equipment);
        var saved = jpa.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        if (jpa.existsById(id)) {
            jpa.deleteById(id);
        } else {
            throw new RuntimeException("Equipment not found: " + id);
        }
    }
}
