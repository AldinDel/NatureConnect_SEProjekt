package at.fhv.Event.config;

import at.fhv.Event.application.user.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Erlaubt @PreAuthorize in Controllern
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Role-based access control:
        // - ADMIN: Full access to all operations
        // - FRONT: Frontend staff - can edit events but not create/cancel (see UserPermissionService)
        // - ORGANIZER: Can create and manage their own events
        // - CUSTOMER: Can view events and make bookings (no event management)
        http
                .authorizeHttpRequests(auth -> auth
                        // Statische Ressourcen
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        // Öffentliche Seiten
                        .requestMatchers("/", "/events", "/events/search", "/events/{id}", "/register", "/login").permitAll()

                        // Create Event: Nur Admin & Organizer
                        .requestMatchers("/events/new", "/events/backoffice").hasAnyRole("ADMIN", "ORGANIZER")

                        // Edit/Cancel: Admin, Front, Organizer (Details im Controller via UserPermissionService)
                        .requestMatchers("/events/{id}/edit", "/events/{id}/cancel").hasAnyRole("ADMIN", "FRONT", "ORGANIZER")

                        // Booking: Alle authentifizierten Benutzer (inkl. CUSTOMER)
                        .requestMatchers("/booking/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}