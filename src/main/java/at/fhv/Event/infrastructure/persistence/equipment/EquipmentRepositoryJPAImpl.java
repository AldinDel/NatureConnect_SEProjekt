package at.fhv.Event.infrastructure.persistence.equipment;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.infrastructure.mapper.EquipmentMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
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
    public Optional<Equipment> findByNameIgnoreCase(String name) {
        return jpa.findByNameIgnoreCase(name)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Equipment> findById(Long id) {
        return jpa.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Equipment> findAll() {
        return jpa.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Equipment> findByRentableTrue() {
        return jpa.findByRentableTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Map<Long, Equipment> findByIds(List<Long> ids) {
        return jpa.findAllById(ids)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toMap(
                        Equipment::getId,
                        equipment -> equipment
                ));
    }

    @Override
    public Equipment save(Equipment equipment) {
        EquipmentEntity entity = mapper.toEntity(equipment);
        EquipmentEntity savedEntity = jpa.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void updateStock(Long equipmentId, int newStock) {
        EquipmentEntity entity = jpa.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Equipment not found: " + equipmentId
                ));

        entity.setStock(newStock);
        jpa.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        if (jpa.existsById(id)) {
            jpa.deleteById(id);
        } else {
            throw new IllegalArgumentException("Equipment not found: " + id);
        }
    }
}
