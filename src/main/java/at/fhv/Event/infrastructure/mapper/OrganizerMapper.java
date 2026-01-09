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

        entity.setId(domain.getId());
        entity.setUser(UserMapper.toEntity(domain.getUser()));
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setActive(domain.getActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}
