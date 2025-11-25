package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.user.CustomerProfile;
import at.fhv.Event.infrastructure.persistence.user.CustomerProfileEntity;

public class CustomerProfileMapper {
    public static CustomerProfile toDomain(CustomerProfileEntity entity) {
        if (entity == null) {
            return null;
        }

        return new CustomerProfile(
                entity.getId(),
                UserMapper.toDomain(entity.getUser()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getBirthday(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static CustomerProfileEntity toEntity(CustomerProfile domain) {
        if (domain == null) {
            return null;
        }

        CustomerProfileEntity entity = new CustomerProfileEntity();

        entity.setId(domain.get_id());
        entity.setUser(UserMapper.toEntity(domain.get_user()));
        entity.setFirstName(domain.get_firstName());
        entity.setLastName(domain.get_lastName());
        entity.setEmail(domain.get_email());
        entity.setPhone(domain.get_phone());
        entity.setBirthday(domain.get_birthday());
        entity.setCreatedAt(domain.get_createdAt());
        entity.setUpdatedAt(domain.get_updatedAt());

        return entity;
    }
}
