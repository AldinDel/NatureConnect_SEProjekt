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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/", "/events", "/events/search", "/events/*", "/register", "/login").permitAll()
                        .requestMatchers("/bookings").permitAll()
                        .requestMatchers("/booking/guest-info/**").permitAll()
                        .requestMatchers("/booking/*").permitAll()
                        .requestMatchers("/bookings/all").hasAnyRole("ADMIN", "FRONT", "ORGANIZER")
                        .requestMatchers("/events/new", "/events/backoffice").hasAnyRole("ADMIN", "ORGANIZER")
                        .requestMatchers("/api/bookings").permitAll()
                        .requestMatchers("/events/*/edit", "/events/*/cancel").hasAnyRole("ADMIN", "FRONT", "ORGANIZER")
                        .requestMatchers("/api/webhooks/payment").permitAll()
                        .requestMatchers("/booking/payment/**").authenticated()
                        .requestMatchers("/booking/confirmation/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((req, res, auth) -> {

                            String redirect = req.getParameter("redirect");

                            if (redirect != null && !redirect.isBlank()) {
                                res.sendRedirect(redirect);
                                return;
                            }

                            var saved = (SavedRequest) req.getSession()
                                    .getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                            if (saved != null) {
                                res.sendRedirect(saved.getRedirectUrl());
                                return;
                            }

                            res.sendRedirect("/");
                        })

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