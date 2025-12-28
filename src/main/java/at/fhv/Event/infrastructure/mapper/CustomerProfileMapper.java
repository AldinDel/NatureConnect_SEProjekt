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

        entity.setId(domain.getId());
        entity.setUser(UserMapper.toEntity(domain.getUser()));
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setBirthday(domain.getBirthday());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}
