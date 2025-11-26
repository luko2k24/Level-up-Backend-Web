package com.tuempresa.tienda.config;

import com.tuempresa.tienda.util.JwtAuthenticationFilter;
import com.tuempresa.tienda.util.JwtUtil;
import com.tuempresa.tienda.servicio.UsuarioServicio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// NOTA: Se eliminaron las importaciones de CorsRegistry y WebMvcConfigurer para resolver el conflicto.

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

    // ✅ Password encoder para hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Autenticación con usuarios de la BD
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioServicio);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ✅ Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // ✅ Filtro JWT
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, usuarioServicio);
    }

    // ✅ Configuración de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // ⚠️ necesario para APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth
                        // ✅ Login, registro, refresh token: públicos
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // ✅ Productos visibles sin iniciar sesión
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/{id}")
                        .permitAll()

                        // ✅ Crear pedido → requiere login
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos")
                        .authenticated()

                        // ✅ Swagger público
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                        .permitAll()

                        // ✅ Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                );

        // ✅ Agregar filtro JWT antes del de usuario
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}