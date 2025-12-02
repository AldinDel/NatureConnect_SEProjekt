package at.fhv.Event.application.equipment;

import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllEquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapperDTO mapper;

    public GetAllEquipmentService(EquipmentRepository equipmentRepository, EquipmentMapperDTO mapper) {
        this.equipmentRepository = equipmentRepository;
        this.mapper = mapper;
    }

    public List<EquipmentDTO> getAll() {
        return equipmentRepository.findAll().stream().map(e -> mapper.toDto(e, false)).collect(Collectors.toList());
    }
}
