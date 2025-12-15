package at.fhv.Event.application.user;

import at.fhv.Event.application.request.user.AdminUserEditDTO;
import at.fhv.Event.infrastructure.persistence.user.UserAccountEntity;
import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAdminUserForEditService {

    private final UserAccountJpaRepository userRepo;

    public GetAdminUserForEditService(UserAccountJpaRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public AdminUserEditDTO getById(Long id) {
        UserAccountEntity u = userRepo.findById(id).orElseThrow();

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
