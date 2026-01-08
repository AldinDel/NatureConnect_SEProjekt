package at.fhv.Event.application.equipment;

import at.fhv.Event.application.request.equipment.CreateEquipmentRequest;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.exception.EquipmentCreationException;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;
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
            throw new EquipmentCreationException(req.getName(), "Equipment name is required");
        }
        equipmentRepository.findByNameIgnoreCase(req.getName()).ifPresent(e -> {
            throw new EquipmentCreationException(req.getName(), "Equipment already exists: ");
        });
        Equipment domain = mapper.toDomainCreate(req.getName(), req.getUnitPrice(), req.isRentable(), req.getStock());
        equipmentRepository.save(domain);
        return mapper.toDto(domain, false);
    }
}
