package at.fhv.Event.application.equipment;

import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Service;

@Service
public class GetEquipmentDetailsService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapperDTO mapper;

    public GetEquipmentDetailsService(EquipmentRepository equipmentRepository, EquipmentMapperDTO mapper) {
        this.equipmentRepository = equipmentRepository;
        this.mapper = mapper;
    }

    public EquipmentDTO getById(Long id) {
        var e = equipmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Equipment not found: " + id));
        return mapper.toDto(e, false);
    }
}
