package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;

@Service
public class GetEventDetailsService {

    private final EventRepository eventRepository;
    private final EventMapperDTO mapper;
    private final EquipmentJpaRepository equipmentJpa;

    public GetEventDetailsService(EventRepository eventRepository, EventMapperDTO mapper,
                                  EquipmentJpaRepository equipmentJpa) {
        this.eventRepository = eventRepository;
        this.mapper = mapper;
        this.equipmentJpa = equipmentJpa;
    }

    public EventDetailDTO getEventDetails(Long eventId) {
        var eventOptional = eventRepository.findByIdWithEquipments(eventId);

        if (eventOptional.isEmpty()) {
            throw new RuntimeException("Event not found: " + eventId);
        }

        var event = eventOptional.get();

        event.getEventEquipments().forEach(ee -> {
            var fresh = equipmentJpa.findById(ee.getEquipment().getId()).orElseThrow();
            ee.getEquipment().setStock(fresh.getStock());
        });

        return mapper.toDetailDTO(event);
    }

}