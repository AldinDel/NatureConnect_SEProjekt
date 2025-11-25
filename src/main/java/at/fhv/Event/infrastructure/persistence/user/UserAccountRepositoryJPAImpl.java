package at.fhv.Event.infrastructure.persistence.user;

import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.domain.model.user.UserAccountRepository;
import at.fhv.Event.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserAccountRepositoryJPAImpl implements UserAccountRepository {

    private final UserAccountJpaRepository jpaRepository;

    public UserAccountRepositoryJPAImpl(UserAccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserAccount> findByEmailIgnoreCase(String email) {

        Optional<UserAccountEntity> entityOptional =
                jpaRepository.findByEmailIgnoreCase(email);

        if (entityOptional.isEmpty()) {
            return Optional.empty();
        }

        UserAccountEntity entity = entityOptional.get();
        UserAccount domain = UserMapper.toDomain(entity);

        return Optional.of(domain);
    }
}
