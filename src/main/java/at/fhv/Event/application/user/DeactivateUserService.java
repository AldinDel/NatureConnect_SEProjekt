package at.fhv.Event.application.user;

import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.domain.model.user.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateUserService {

    private final UserAccountRepository userRepo;

    public DeactivateUserService(UserAccountRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public void deactivate(Long userId) {
        UserAccount user = userRepo.findById(userId).orElseThrow();
        user.setActive(false);
        userRepo.save(user);
    }
}
