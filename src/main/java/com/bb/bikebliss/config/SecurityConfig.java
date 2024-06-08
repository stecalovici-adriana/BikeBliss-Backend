package com.bb.bikebliss.config;

import com.bb.bikebliss.filter.JwtAuthenticationFilter;
import com.bb.bikebliss.service.implementation.UserDetailsServiceImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final UserDetailsServiceImp userDetailsServiceImp;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomLogoutHandler logoutHandler;

    public SecurityConfig(UserDetailsServiceImp userDetailsServiceImp,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomLogoutHandler logoutHandler) {
        this.userDetailsServiceImp = userDetailsServiceImp;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:3000"));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/user/**")
                        .hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/feedback/**").permitAll()
                        .requestMatchers("/api/bikes/addModels",
                                "/api/locations/addLocation","/api/equipments/addEquipmentModels",
                                "/api/rentals/approveRental/{rentalId}", "/api/rentals/rejectRental/{rentalId}",
                                "/api/equipmentRentals/approveEquipmentRental/{equipmentRentalId}",
                                "/api/equipmentRentals/rejectEquipmentRental/{equipmentRentalId}")
                        .hasAuthority("ADMIN")
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/verify",
                                "/api/auth/forgot-password", "/api/auth/reset-password",
                                "/api/bikes/models", "/api/bikes/models/{modelId}",
                                "/api/rentals/unavailable-dates/{modelId}","/api/rentals/cancelRental/**",
                                "/api/rentals/sendEndRentalReminders","/api/rentals/createRental/{modelId}",
                                "/api/rentals/active-rentals", "/api/rentals/pending-rentals", "/api/rentals/completed-rentals",
                                "/api/equipments/equipmentModels", "/api/equipments/equipmentModels/{equipmentModelId}",
                                "/api/equipmentRentals/unavailable-dates/{equipmentModelId}",
                                "/api/equipmentRentals/cancelEquipmentRental/{equipmentRentalId}",
                                "/api/equipmentRentals/sendEndRentalReminders",
                                "/api/equipmentRentals/createEquipmentRental/{equipmentRentalId}",
                                "/api/equipmentRentals/active-rentals", "/api/equipmentRentals/pending-rentals",
                                "/api/equipmentRentals/completed-rentals")
                        .permitAll()
                        .anyRequest().authenticated()
                ).userDetailsService(userDetailsServiceImp)
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e->e
                        .accessDeniedHandler((request, response, accessDeniedException)->response.setStatus(403))
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .logout(l->l
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()
                        ))
                .build();

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}