package at.fhv.Event.domain.model.user;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository {
    Optional<UserAccount> findById(Long id);
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    UserAccount save(UserAccount user);
    List<UserAccount> findTopByOrderByIdDesc(int limit);
    List<UserAccount> searchAdminUsers(String q, Long idExact, String role, Pageable pageable);
}

