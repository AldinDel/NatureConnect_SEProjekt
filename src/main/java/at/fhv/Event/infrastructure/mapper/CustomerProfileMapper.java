package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.user.CustomerProfile;
import at.fhv.Event.infrastructure.persistence.user.CustomerProfileEntity;

import java.time.OffsetDateTime;

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
                entity.getStreet(),
                entity.getPostalCode(),
                entity.getCity(),
                entity.getCountry(),
                entity.getAvatarUrl(),
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
        entity.setStreet(domain.getStreet());
        entity.setPostalCode(domain.getPostalCode());
        entity.setCity(domain.getCity());
        entity.setCountry(domain.getCountry());
        entity.setAvatarUrl(domain.getAvatarUrl());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(OffsetDateTime.now());

        return entity;
    }

    public static void updateEntity(CustomerProfileEntity entity, CustomerProfile domain) {
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setPhone(domain.getPhone());
        entity.setBirthday(domain.getBirthday());
        entity.setStreet(domain.getStreet());
        entity.setPostalCode(domain.getPostalCode());
        entity.setCity(domain.getCity());
        entity.setCountry(domain.getCountry());
        entity.setAvatarUrl(domain.getAvatarUrl());
        entity.setUpdatedAt(OffsetDateTime.now());
    }
}
