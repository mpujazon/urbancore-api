package com.urbancore.urbancore_api.config;

import com.urbancore.urbancore_api.security.CustomJwtAuthenticationConverter;
import com.urbancore.urbancore_api.security.RestAccessDeniedHandler;
import com.urbancore.urbancore_api.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(
            CustomJwtAuthenticationConverter customJwtAuthenticationConverter,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler
    ) {
        this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/incidents/me").hasRole("CITIZEN")
                        .requestMatchers(HttpMethod.POST, "/api/incidents").hasRole("CITIZEN")
                        .requestMatchers(HttpMethod.POST, "/api/ai/incident-suggestions").hasRole("CITIZEN")
                        .requestMatchers(HttpMethod.PATCH, "/api/incidents/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/incidents/*/priority").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/incidents/*").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/incidents").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/incidents/*").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/planned-actions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/planned-actions/incident/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stats/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/planned-actions").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/planned-actions/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/planned-actions/*").hasRole("ADMIN")

                        .requestMatchers("/api/auth/sync").authenticated()
                        .requestMatchers("/api/uploads/signature").hasRole("CITIZEN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter))
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://urbancore-pi.vercel.app"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
