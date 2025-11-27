package at.fhv.Event.application.equipment;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.rest.mapper.EquipmentResponseMapper;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRentableEquipmentService {
    private final EquipmentRepository _equipmentRepository;
    private final EquipmentResponseMapper _equipmentResponseMapper;

    public GetRentableEquipmentService(EquipmentRepository equipmentRepository, EquipmentResponseMapper equipmentResponseMapper) {
        _equipmentRepository = equipmentRepository;
        _equipmentResponseMapper = equipmentResponseMapper;
    }

    public List<EquipmentDTO> getRentableEquipment() {
        List<Equipment> equipments = _equipmentRepository.findByRentableTrue();
        return _equipmentResponseMapper.toDTOList(equipments);
    }
}
