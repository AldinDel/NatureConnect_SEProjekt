package at.fhv.Event.infrastructure.mapper;
import at.fhv.Event.domain.model.user.Role;
import at.fhv.Event.infrastructure.persistence.user.RoleEntity;

public class RoleMapper {
    public static Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Role(
                entity.getId(),
                entity.getCode()
        );
    }

    public static RoleEntity toEntity(Role domain) {
        if (domain == null) {
            return null;
        }

        RoleEntity entity = new RoleEntity();
        entity.setId(domain.get_id());
        entity.setCode(domain.get_code());

        return entity;
    }
}
