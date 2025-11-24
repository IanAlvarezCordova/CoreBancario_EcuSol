package com.ecusol.ecusolcore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS: Usar nuestra configuración estricta definida abajo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF: Desactivar (No necesario para APIs REST stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Desactivar Login por defecto de Spring Security
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 4. Sesión Stateless (Sin cookies, solo JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. Definición de Rutas
                .authorizeHttpRequests(auth -> auth
                        // Documentación Swagger (Pública)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Endpoints Públicos de Negocio
                        .requestMatchers("/api/auth/**", "/api/ventanilla/**", "/api/util/**").permitAll()

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // 6. Filtro JWT antes del filtro estándar
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CONFIGURACIÓN CORS ESTRICTA (Lista blanca de orígenes)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // LISTADO EXPLÍCITO DE ORÍGENES PERMITIDOS
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",                                      // Vite Local
                "http://localhost:5174",                                      // Vite Local (Alternativo)
                "http://localhost:54698",                                     // Otros puertos locales
                "http://localhost:50256",
                "http://3.16.1.39", "https://3.16.1.39",                                     // BACKEND
                "http://ec2-3-16-1-39.us-east-2.compute.amazonaws.com", "https://ec2-3-16-1-39.us-east-2.compute.amazonaws.com",
                "http://18.217.59.120", "https://18.217.59.120",                                      // SITIOWEB
                "http://ec2-18-217-59-120.us-east-2.compute.amazonaws.com", "https://ec2-18-217-59-120.us-east-2.compute.amazonaws.com",
                "http://3.144.129.57", "https://3.144.129.57", // VENTANILLA
                "http://ec2-3-144-129-57.us-east-2.compute.amazonaws.com", "https://ec2-3-144-129-57.us-east-2.compute.amazonaws.com" // VENTANILLA

        ));

        // Permitir todos los métodos HTTP estándar
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permitir todos los headers necesarios
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));

        // Permitir credenciales (cookies, auth headers) - Obligatorio true si usas lista específica
        configuration.setAllowCredentials(true);

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

    // Configuración de Swagger UI
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API EcuSol Core Bancario")
                        .version("1.0")
                        .description("Documentación de endpoints para Ventanilla y Banca Web"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}