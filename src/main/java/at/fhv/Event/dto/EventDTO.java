package at.fhv.Event.dto;
import java.math.BigDecimal;
import java.util.List;

public record EventDTO(
        Long id,
        String title,
        String description,
        String location,
        BigDecimal price,
        List<EquipmentDTO> equipments
) {}