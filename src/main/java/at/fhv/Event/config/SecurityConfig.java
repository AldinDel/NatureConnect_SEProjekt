package at.fhv.Event.config;

import at.fhv.Event.application.user.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Profile("!test")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Value("${remember.me.secret}")
    private String rememberMeSecret;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/booking/payment/**")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/", "/events", "/events/search", "/events/*", "/register", "/login").permitAll()
                        .requestMatchers("/bookings").permitAll()
                        .requestMatchers("/booking/guest-info/**").permitAll()
                        .requestMatchers("/booking/*").permitAll()
                        .requestMatchers("/admin/users/**").hasRole("ADMIN")
                        .requestMatchers("/bookings/all").hasAnyRole("ADMIN", "FRONT", "ORGANIZER")
                        .requestMatchers("/events/new", "/events/backoffice").hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers("/api/bookings/**").permitAll()
                        .requestMatchers("/api/webhooks/payment").permitAll()
                        .requestMatchers("/api/hiking/**").permitAll()
                        .requestMatchers("/api/events/*/equipment").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/*").permitAll()

                        .requestMatchers("/api/events/**").hasAnyRole("ADMIN", "FRONT", "ORGANIZER")

                        .requestMatchers("/events/*/edit", "/events/*/cancel").hasAnyRole("ADMIN", "FRONT", "ORGANIZER")
                        .requestMatchers("/event_management/**").hasAnyRole("ADMIN", "FRONT")
                        .requestMatchers("/booking/payment/**").permitAll()
                        .requestMatchers("/booking/confirmation/**").permitAll()
                        .requestMatchers("/", "/imprint", "/privacy", "/terms", "/about", "/contact", "/refunds", "/payment-methods").permitAll()

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
                .rememberMe(remember -> remember
                        .rememberMeServices(rememberMeServices())
                        .key(rememberMeSecret)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:6060", "http://localhost:6061", "http://localhost:6062", "http://localhost:6063", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices =
                new TokenBasedRememberMeServices(
                        rememberMeSecret,
                        userDetailsService
                );
        rememberMeServices.setTokenValiditySeconds(2592000);
        rememberMeServices.setCookieName("remember-me");
        rememberMeServices.setParameter("remember-me");
        return rememberMeServices;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}