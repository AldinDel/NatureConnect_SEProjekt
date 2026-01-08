package at.fhv.Event.application.user;

import at.fhv.Event.application.request.user.AdminUserEditDTO;
import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.domain.model.user.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAdminUserForEditService {

    private final UserAccountRepository userRepo;

    public GetAdminUserForEditService(UserAccountRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public AdminUserEditDTO getById(Long id) {
        UserAccount u = userRepo.findById(id).orElseThrow();

        String role = u.getRoles().stream()
                .map(r -> r.getCode())
                .sorted()
                .findFirst()
                .orElse("");

        return new AdminUserEditDTO(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                Boolean.TRUE.equals(u.getActive()),
                role
        );
    }
}
