package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;

@Service
public class GetEventDetailsService {
    private final EventRepository _eventRepository;
    private final EventMapperDTO _mapper;
    private final EquipmentRepository _equipmentRepository;

    public GetEventDetailsService(EventRepository eventRepository, EventMapperDTO mapper, EquipmentRepository equipmentRepository) {
        _eventRepository = eventRepository;
        _mapper = mapper;
        _equipmentRepository = equipmentRepository;
    }

    public EventDetailDTO getEventDetails(Long eventId) {
        Event event = _eventRepository.findByIdWithEquipments(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        for (var eventEquipment : event.getEventEquipments()) {
            Long equipmentId = eventEquipment.getEquipment().getId();
            _equipmentRepository.findById(equipmentId).ifPresent(equipment -> {
                eventEquipment.getEquipment().setStock(equipment.getStock());
            });
        }
        return _mapper.toDetailDTO(event);
    }
}