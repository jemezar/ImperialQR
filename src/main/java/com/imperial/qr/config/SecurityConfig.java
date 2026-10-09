package com.imperial.qr.config;

import com.imperial.qr.security.CustomUserDetailsService;
import com.imperial.qr.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Documentación Swagger / OpenAPI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // Auth
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Endpoints públicos para clientes por código QR y catálogo
                .requestMatchers(HttpMethod.GET, "/api/v1/carta").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/mesas/qr/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/ordenes").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ordenes/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/ordenes/*/pagos").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/ordenes/*/encuesta").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/domicilios").permitAll()

                // Endpoints Cocina
                .requestMatchers("/api/v1/cocina/**").hasAnyRole("COCINERO", "ADMIN")

                // Endpoints Mesero
                .requestMatchers("/api/v1/mesero/**").hasAnyRole("MESERO", "ADMIN")

                // Cambio de estado de plato: roles de staff según la regla RN-06
                .requestMatchers(HttpMethod.PATCH, "/api/v1/detalles/*/estado").hasAnyRole("COCINERO", "MESERO", "DOMICILIARIO", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/detalles/*/cancelar").hasRole("ADMIN")

                // Cierre de cuenta por el administrador
                .requestMatchers(HttpMethod.POST, "/api/v1/ordenes/*/cierre").hasRole("ADMIN")

                // Gestión de domicilios
                .requestMatchers(HttpMethod.PATCH, "/api/v1/domicilios/*").hasAnyRole("ADMIN", "DOMICILIARIO")

                // Dashboard general, reportes, encuestas resumen y CRUD administrativo
                .requestMatchers("/api/v1/ordenes/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/encuestas/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/reportes/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/platos/**", "/api/v1/categorias/**", "/api/v1/ingredientes/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/mesas/**", "/api/v1/usuarios/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
