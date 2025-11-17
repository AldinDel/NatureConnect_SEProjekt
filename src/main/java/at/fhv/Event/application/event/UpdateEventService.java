package at.fhv.Event.application.event;

import at.fhv.Event.application.request.event.UpdateEventRequest;
import at.fhv.Event.domain.model.event.EventAudience;
import at.fhv.Event.infrastructure.mapper.EventMapper;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.infrastructure.persistence.equipment.EventEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import at.fhv.Event.infrastructure.persistence.event.EventJpaRepository;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateEventService {

    private final EventJpaRepository eventJpaRepository;
    private final EquipmentJpaRepository equipmentJpaRepository;
    private final EventMapper domainMapper;       // Entity <-> Domain
    private final EventMapperDTO dtoMapper;       // Domain <-> DTO

    public UpdateEventService(EventJpaRepository eventJpaRepository,
                              EquipmentJpaRepository equipmentJpaRepository,
                              EventMapper domainMapper,
                              EventMapperDTO dtoMapper) {
        this.eventJpaRepository = eventJpaRepository;
        this.equipmentJpaRepository = equipmentJpaRepository;
        this.domainMapper = domainMapper;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public EventDetailDTO updateEvent(Long id, UpdateEventRequest req) {

        EventEntity entity = eventJpaRepository.findByIdWithEquipments(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        // Update basic fields
        entity.setTitle(req.getTitle());
        entity.setDescription(req.getDescription());
        entity.setOrganizer(req.getOrganizer());
        entity.setCategory(req.getCategory());
        entity.setDate(req.getDate());
        entity.setStartTime(req.getStartTime());
        entity.setEndTime(req.getEndTime());
        entity.setLocation(req.getLocation());
        entity.setDifficulty(req.getDifficulty());
        entity.setMinParticipants(req.getMinParticipants());
        entity.setMaxParticipants(req.getMaxParticipants());
        entity.setPrice(req.getPrice());
        entity.setImageUrl(req.getImageUrl());

        if (req.getAudience() != null && !req.getAudience().isBlank()) {
            entity.setAudience(EventAudience.valueOf(req.getAudience()));
        }

        System.out.println("=== DEBUG: Incoming Equipment from Request ===");
        if (req.getEquipments() == null) {
            System.out.println("req.getEquipments() = NULL");
        } else {
            System.out.println("req.getEquipments().size() = " + req.getEquipments().size());
            for (var e : req.getEquipments()) {
                System.out.println(" - Equipment:");
                System.out.println("     id       = " + e.getId());
                System.out.println("     name     = " + e.getName());
                System.out.println("     price    = " + e.getPrice());
                System.out.println("     rentable = " + e.isRentable());
                System.out.println("     required = " + e.isRequired());
            }
        }
        System.out.println("=============================================");

        // CRITICAL FIX: Remove old equipment references manually
        // Copy to avoid ConcurrentModificationException
        var oldEquipments = new java.util.ArrayList<>(entity.getEventEquipments());
        for (var old : oldEquipments) {
            entity.removeEquipment(old);  // Use the remove method if you have one
        }
        entity.getEventEquipments().clear();

        // Flush to ensure deletions are processed
        eventJpaRepository.flush();

        // Add new equipment
        if (req.getEquipments() != null && !req.getEquipments().isEmpty()) {
            for (var eqReq : req.getEquipments()) {
                if (eqReq.getId() == null || eqReq.getId() <= 0) {
                    System.out.println("Skipping equipment with null/invalid ID: " + eqReq.getName());
                    continue;
                }

                var equipment = equipmentJpaRepository.findById(eqReq.getId())
                        .orElseThrow(() -> new RuntimeException("Equipment not found: " + eqReq.getId()));

                // Create completely new EventEquipmentEntity
                var eventEquipment = new EventEquipmentEntity(entity, equipment, eqReq.isRequired());
                entity.addEquipment(eventEquipment);
                System.out.println("Added equipment link: " + eqReq.getName() + " (required=" + eqReq.isRequired() + ")");
            }
        }

        EventEntity saved = eventJpaRepository.save(entity);
        return dtoMapper.toDetailDTO(domainMapper.toDomain(saved));
    }}