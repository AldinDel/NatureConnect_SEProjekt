package at.fhv.Event.application.user;

import at.fhv.Event.infrastructure.persistence.user.UserAccountEntity;
import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateUserService {

    private final UserAccountJpaRepository userRepo;

    public DeactivateUserService(UserAccountJpaRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public void deactivate(Long userId) {
        UserAccountEntity user = userRepo.findById(userId).orElseThrow();
        user.setActive(false);
        userRepo.save(user);
    }
}
