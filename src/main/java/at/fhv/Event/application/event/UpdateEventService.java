package at.fhv.Event.application.event;

import at.fhv.Event.application.request.event.UpdateEventRequest;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventAudience;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.*;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UpdateEventService {
    private final EventRepository eventRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventMapperDTO dtoMapper;
    private final EventValidator eventValidator;

    public UpdateEventService(EventRepository eventRepository, EquipmentRepository equipmentRepository,
            EventMapperDTO dtoMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.equipmentRepository = equipmentRepository;
        this.dtoMapper = dtoMapper;
        this.eventValidator = eventValidator;
    }

    @Transactional
    public EventDetailDTO updateEvent(Long id, UpdateEventRequest req) {
        List<ValidationError> errors = eventValidator.validate(req);
        if (!errors.isEmpty()) {
            throw new EventValidationException(errors);
        }

        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));

        if (Boolean.TRUE.equals(event.getCancelled())) {
            throw new EventAlreadyCancelledException(id);
        }

        event.setTitle(req.getTitle());
        event.setDescription(req.getDescription());
        event.setOrganizer(req.getOrganizer());
        event.setCategory(req.getCategory());
        event.setDate(req.getDate());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        event.setLocation(req.getLocation());
        event.setDifficulty(req.getDifficulty());
        event.setMinParticipants(req.getMinParticipants());
        event.setMaxParticipants(req.getMaxParticipants());
        event.setPrice(req.getPrice());
        event.setImageUrl(req.getImageUrl());
        event.setHikeRouteKeys(req.getHikeRouteKeys());


        if (req.getAudience() != null && !req.getAudience().isBlank()) {
            event.setAudience(EventAudience.valueOf(req.getAudience()));
        } else {
            event.setAudience(null);
        }

        if (req.getEquipments() != null && !req.getEquipments().isEmpty()) {
            updateEventEquipments(event, req);
        }

        Event saved = eventRepository.save(event);
        return dtoMapper.toDetailDTO(saved);
    }

    private void updateEventEquipments(Event event, UpdateEventRequest req) {
        event.getEventEquipments().clear();
        for (var eqReq : req.getEquipments()) {
            if (eqReq.getName() == null || eqReq.getName().isBlank()) {
                continue;
            }

            if (eqReq.isRentable()) {
                if (eqReq.getUnitPrice() == null) {
                    throw new IllegalArgumentException("Unit price is required when equipment is rentable: " + eqReq.getName());
                }
                if (eqReq.getStock() == null) {
                    throw new IllegalArgumentException("Stock is required when equipment is rentable: " + eqReq.getName());
                }
            }
            Equipment equipment;
            if (eqReq.getId() == null) {
                equipment = new Equipment(
                        null,
                        eqReq.getName(),
                        eqReq.getUnitPrice(),
                        eqReq.isRentable(),
                        eqReq.getStock()
                );
                equipment = equipmentRepository.save(equipment);
            } else {
                equipment = equipmentRepository.findById(eqReq.getId()).orElseThrow(() -> new EquipmentNotFoundException(eqReq.getId()));
                equipment.setRentable(eqReq.isRentable());
                if (eqReq.isRentable()) {
                    equipment.setUnitPrice(eqReq.getUnitPrice());
                    equipment.setStock(eqReq.getStock());
                } else {
                    equipment.setUnitPrice(null);
                    equipment.setStock(null);
                }
                equipment = equipmentRepository.save(equipment);
            }

            EventEquipment eventEquipment = new EventEquipment(equipment, eqReq.isRequired());
            event.getEventEquipments().add(eventEquipment);
        }
    }
}