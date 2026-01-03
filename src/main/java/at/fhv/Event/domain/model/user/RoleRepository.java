package at.fhv.Event.domain.model.user;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByCode(String code);
}
