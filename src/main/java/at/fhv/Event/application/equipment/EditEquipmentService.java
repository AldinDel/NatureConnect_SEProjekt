package at.fhv.Event.application.equipment;

import at.fhv.Event.application.request.equipment.UpdateEquipmentRequest;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditEquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapperDTO mapper;

    public EditEquipmentService(EquipmentRepository equipmentRepository, EquipmentMapperDTO mapper) {
        this.equipmentRepository = equipmentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EquipmentDTO edit(Long id, UpdateEquipmentRequest req) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found: " + id));
        Equipment updated = mapper.toDomainUpdate(existing.getId(), req.getName(), req.getUnitPrice(), req.isRentable(), req.getStock());
        equipmentRepository.save(updated);
        return mapper.toDto(updated, false);
    }
}
