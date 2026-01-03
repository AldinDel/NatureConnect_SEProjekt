package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.user.Role;
import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.infrastructure.persistence.user.RoleEntity;
import at.fhv.Event.infrastructure.persistence.user.UserAccountEntity;

import java.util.HashSet;
import java.util.Set;

public class UserMapper {
    public static UserAccount toDomain(UserAccountEntity entity) {
        if (entity == null) {
            return null;
        }

        UserAccount domain = new UserAccount(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );

        Set<Role> domainRoles = new HashSet<>();
        for (RoleEntity r : entity.getRoles()) {
            Role domainRole = RoleMapper.toDomain(r);
            domainRoles.add(domainRole);
        }

        domain.setRoles(domainRoles);

        return domain;
    }

    public static UserAccountEntity toEntity(UserAccount domain) {
        if (domain == null) {
            return null;
        }

        UserAccountEntity entity = new UserAccountEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setActive(domain.getActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        Set<RoleEntity> entityRoles = new HashSet<>();
        for (Role r : domain.getRoles()) {
            RoleEntity roleEntity = RoleMapper.toEntity(r);
            entityRoles.add(roleEntity);
        }

        entity.setRoles(entityRoles);

        return entity;
    }
}
