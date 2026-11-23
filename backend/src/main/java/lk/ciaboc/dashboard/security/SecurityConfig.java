package lk.ciaboc.dashboard.security;

import lk.ciaboc.dashboard.auth.AuthService;
import lk.ciaboc.dashboard.auth.SsoSuccessHandler;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${dashboard.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthService authService,
                                                   Optional<ClientRegistrationRepository> clientRegistrations) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/health", "/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/api/v1/auth/session").permitAll()
                        .requestMatchers("/api/v1/auth/local/login").permitAll()
                        .requestMatchers("/api/v1/auth/role/select",
                                "/api/v1/auth/role/verify", "/api/v1/auth/logout",
                                "/api/v1/auth/admin/**").authenticated()
                        .requestMatchers("/login", "/login/**", "/oauth2/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll());
        http.addFilterAfter(new RoleContextFilter(), UsernamePasswordAuthenticationFilter.class);
        if (clientRegistrations.isPresent()) {
            http.oauth2Login(oauth -> oauth
                    .loginPage("/oauth2/authorization/ciaboc")
                    .successHandler(new SsoSuccessHandler(authService))
                    .failureUrl("http://localhost:3000/login?error=sso"));
        }
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
