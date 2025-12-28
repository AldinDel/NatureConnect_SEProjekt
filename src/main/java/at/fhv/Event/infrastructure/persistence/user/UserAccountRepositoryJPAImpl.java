package at.fhv.Event.infrastructure.persistence.user;

import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.domain.model.user.UserAccountRepository;
import at.fhv.Event.infrastructure.mapper.UserMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserAccountRepositoryJPAImpl implements UserAccountRepository {

    private final UserAccountJpaRepository jpaRepository;

    public UserAccountRepositoryJPAImpl(UserAccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        return jpaRepository.findById(id)
                .map(UserMapper::toDomain);
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

    @Override
    public UserAccount save(UserAccount user) {
        UserAccountEntity entity = UserMapper.toEntity(user);
        UserAccountEntity saved = jpaRepository.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public List<UserAccount> findTopByOrderByIdDesc(int limit) {
        return jpaRepository.findTop5ByOrderByIdDesc().stream()
                .limit(limit)
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public List<UserAccount> searchAdminUsers(
            String q,
            Long idExact,
            String role,
            Pageable pageable
    ) {
        return jpaRepository.searchAdminUsers(q, idExact, role, pageable)
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }
}
