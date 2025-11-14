package at.fhv.Event.application.equipment;

import at.fhv.Event.application.request.equipment.CreateEquipmentRequest;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateEquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapperDTO mapper;

    public CreateEquipmentService(EquipmentRepository equipmentRepository, EquipmentMapperDTO mapper) {
        this.equipmentRepository = equipmentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EquipmentDTO create(CreateEquipmentRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new RuntimeException("Equipment name is required");
        }
        equipmentRepository.findByNameIgnoreCase(req.getName()).ifPresent(e -> {
            throw new RuntimeException("Equipment already exists: " + req.getName());
        });
        Equipment domain = mapper.toDomainCreate(req.getName(), req.getUnitPrice(), req.isRentable());
        equipmentRepository.save(domain);
        // map back to DTO; required=false for stand-alone equipment
        return mapper.toDto(domain, false);
    }
}
