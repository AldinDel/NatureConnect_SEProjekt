package at.fhv.Event.application.user;

import at.fhv.Event.application.request.user.AdminUserRowDTO;
import at.fhv.Event.infrastructure.persistence.user.UserAccountEntity;
import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAdminUsersService {

    private final UserAccountJpaRepository userRepo;

    public GetAdminUsersService(UserAccountJpaRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<AdminUserRowDTO> getLatestUsers(int limit) {
        List<UserAccountEntity> users = userRepo.findTop5ByOrderByIdDesc();
        return users.stream()
                .limit(limit)
                .map(u -> new AdminUserRowDTO(
                        u.getId(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getEmail(),
                        Boolean.TRUE.equals(u.getActive()),
                        u.getRoles().stream().map(r -> r.getCode()).sorted().collect(Collectors.joining(", "))
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminUserRowDTO> search(String q, String role, int limit) {
        String cleanedQ = (q == null || q.trim().isEmpty()) ? null : q.trim();
        String cleanedRole = (role == null || role.trim().isEmpty() || role.equalsIgnoreCase("all") || role.equalsIgnoreCase("all roles")) ? null : role.trim();

        Long idExact = null;
        if (cleanedQ != null && cleanedQ.matches("\\d+")) {
            try {
                idExact = Long.parseLong(cleanedQ);
            } catch (NumberFormatException ignored) {
                idExact = null;
            }
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<UserAccountEntity> found = userRepo.searchAdminUsers(cleanedQ, idExact, cleanedRole, pageable);
        List<Long> ids = found.stream().map(UserAccountEntity::getId).toList();
        List<UserAccountEntity> users = ids.isEmpty() ? List.of() : userRepo.findAllByIdIn(ids);

        users = users.stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .toList();


        return users.stream()
                .map(u -> new AdminUserRowDTO(
                        u.getId(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getEmail(),
                        Boolean.TRUE.equals(u.getActive()),
                        u.getRoles().stream().map(r -> r.getCode()).sorted().collect(Collectors.joining(", "))
                ))
                .toList();
    }


}
