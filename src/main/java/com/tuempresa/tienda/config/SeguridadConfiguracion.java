package com.tuempresa.tienda.config;

import com.tuempresa.tienda.servicio.UsuarioServicio;
import com.tuempresa.tienda.util.JwtAuthenticationFilter;
import com.tuempresa.tienda.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SeguridadConfiguracion {

    private final JwtUtil jwtUtil;
    private final UsuarioServicio usuarioServicio;

    public SeguridadConfiguracion(JwtUtil jwtUtil, UsuarioServicio usuarioServicio) {
        this.jwtUtil = jwtUtil;
        this.usuarioServicio = usuarioServicio;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioServicio);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, usuarioServicio);
    }

    // ✅ CORS GLOBAL: permite Authorization + POST/PUT/DELETE/OPTIONS desde el front
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Ajusta si tu front corre en otra URL
        config.setAllowedOrigins(List.of("http://localhost:5173"));

        // Importante para que pase el preflight de axios con JSON
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Authorization es clave para JWT
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));

        // Opcional, pero útil si algún día devuelves headers custom
        config.setExposedHeaders(List.of("Authorization"));

        // Si usas cookies/sesión; con JWT en header puede ser false,
        // pero no molesta si lo dejas true para dev.
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        // ✅ CLAVE: permitir preflight CORS
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔓 AUTH
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 🔓 PRODUCTOS (PÚBLICO)
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/productos",
                                "/api/v1/productos/{id}"
                        ).permitAll()

                        // 🔓 CHECKOUT PÚBLICO
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/pedidos/publico"
                        ).permitAll()

                        // 🔒 ADMIN (solo /api/v1/admin/**)
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        // 🔓 SWAGGER
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 🔒 TODO LO DEMÁS
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}