package at.fhv.Event.application.user;

import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.infrastructure.persistence.user.UserAccountEntity;
import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import at.fhv.Event.infrastructure.mapper.UserMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountJpaRepository userRepo;

    public CustomUserDetailsService(UserAccountJpaRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccountEntity userEntity = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        UserAccount user = UserMapper.toDomain(userEntity);

        Set<GrantedAuthority> authorities = user.get_roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.get_code()))
                .collect(Collectors.toSet());

        return new User(
                user.get_email(),
                user.get_passwordHash(),
                user.get_IsActive(),
                true, true, true,
                authorities
        );
    }
}