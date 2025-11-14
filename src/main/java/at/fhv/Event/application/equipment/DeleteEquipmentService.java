package at.fhv.Event.application.equipment;

import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteEquipmentService {

    private final EquipmentRepository equipmentRepository;

    public DeleteEquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional
    public void delete(Long id) {
        equipmentRepository.deleteById(id);
    }
}
