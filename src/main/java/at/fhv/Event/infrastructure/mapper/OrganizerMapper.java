package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.user.Organizer;
import at.fhv.Event.infrastructure.persistence.user.OrganizerEntity;

public class OrganizerMapper {
    public static Organizer toDomain(OrganizerEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Organizer(
                entity.getId(),
                UserMapper.toDomain(entity.getUser()),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static OrganizerEntity toEntity(Organizer domain) {
        if (domain == null) {
            return null;
        }

        OrganizerEntity entity = new OrganizerEntity();

        entity.setId(domain.get_id());
        entity.setUser(UserMapper.toEntity(domain.get_user()));
        entity.setName(domain.get_name());
        entity.setEmail(domain.get_email());
        entity.setPhone(domain.get_phone());
        entity.setActive(domain.get_isActive());
        entity.setCreatedAt(domain.get_createdAt());
        entity.setUpdatedAt(domain.get_updatedAt());

        return entity;
    }
}
