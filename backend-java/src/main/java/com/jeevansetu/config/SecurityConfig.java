package com.jeevansetu.config;

import com.jeevansetu.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public Auth and Documentation
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/assistant/**").permitAll()
                        .requestMatchers("/api/agency-comms/**").permitAll()
                        .requestMatchers("/api/emergencies/sos").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        // Public read-only queries for home page telemetry & maps
                        .requestMatchers(HttpMethod.GET, "/api/analytics/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/resources/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hospitals/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blood-banks/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/shelters/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/emergencies/priority-queue").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/geofence/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/disaster/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/disaster/centers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/disaster/overview").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inventory/overview").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/volunteers/overview").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blood-coordination/overview").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/emergencies/check-duplicate").permitAll()

                        // Real Maps & Public Safety Subsystems
                        .requestMatchers("/api/maps/**").permitAll()
                        .requestMatchers("/api/evacuation/**").permitAll()
                        .requestMatchers("/api/reunification/**").permitAll()
                        .requestMatchers("/api/simulation/**").permitAll()
                        .requestMatchers("/api/public-safety/**").permitAll()
                        .requestMatchers("/api/preparedness/**").permitAll()
                        .requestMatchers("/api/qr/**").permitAll()
                        .requestMatchers("/api/safezone/**").permitAll()
                        .requestMatchers("/api/sla/**").permitAll()
                        .requestMatchers("/api/relief-distribution/**").permitAll()
                        .requestMatchers("/api/post-incident/**").permitAll()

                        // Static web frontend assets
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/assets/**", "/favicon.ico").permitAll()

                        // Role-based restrictions can be specified or enforced via method security / controllers
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
