package com.proyectospring.gestionbodega.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Habilitar CORS y desactivar CSRF
            .cors(org.springframework.security.config.Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // 1. Rutas públicas
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/auth/**", "/api/auth/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/", "/index.html", "/app.js", "/styles.css", "/favicon.ico").permitAll()

            // 2. LECTURA (GET): Ambos roles pueden ver
            .requestMatchers(HttpMethod.GET, "/api/bodegas/**", "/api/productos/**", "/api/movimientos/**").hasAnyRole("ADMIN", "EMPLEADO")

            // 3. OPERACIÓN (POST): Ambos roles pueden registrar (ej. realizar un movimiento)
            .requestMatchers(HttpMethod.POST, "/api/movimientos/**").hasAnyRole("ADMIN", "EMPLEADO")

            // 4. ADMINISTRACIÓN EXCLUSIVA: Solo ADMIN puede modificar o borrar
            .requestMatchers(HttpMethod.PUT, "/api/movimientos/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/movimientos/**").hasRole("ADMIN")
            
            // (Opcional: Si quieres proteger Bodegas/Productos para ediciones solo ADMIN)
            .requestMatchers(HttpMethod.POST, "/api/bodegas/**", "/api/productos/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/bodegas/**", "/api/productos/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/bodegas/**", "/api/productos/**").hasRole("ADMIN")

            // 5. Reportes (Solo ADMIN)
            .requestMatchers("/api/reportes/**").hasRole("ADMIN")

            // 6. Cualquier otra ruta requiere autenticación
            .anyRequest().authenticated()
            );

        // Agregamos el filtro personalizado de JWT antes del filtro estándar
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOriginPatterns(java.util.List.of("*"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        configuration.setAllowCredentials(true);
        
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}