package at.fhv.Event.infrastructure.persistence.user;

import at.fhv.Event.domain.model.user.RoleRepository;
import at.fhv.Event.domain.model.user.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleJpaRepository jpaRepo;

    public RoleRepositoryImpl(RoleJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return jpaRepo.findByCode(code)
                .map(this::toDomain);
    }

    private Role toDomain(RoleEntity entity) {
        return new Role(entity.getId(), entity.getCode());
    }
}
