package at.fhv.Event.domain.model.user;

import java.util.Optional;

public interface UserAccountRepository {
    Optional<UserAccount> findByEmailIgnoreCase(String email);
}

