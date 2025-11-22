package com.tuempresa.tienda.util;

import com.tuempresa.tienda.servicio.UsuarioServicio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtro que se ejecuta en cada petición para validar el JWT en el encabezado.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Spring inyectará estas dependencias automáticamente
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener el encabezado de autorización
        String header = request.getHeader("Authorization");
        String token = null;
        String nombreUsuario = null;

        // 2. Validar formato (Debe ser "Bearer [token]")
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                // 3. Extraer el nombre de usuario del token
                nombreUsuario = jwtUtil.obtenerNombreUsuario(token);
            } catch (Exception e) {
                // El token es inválido o expiró
            }
        }

        // 4. Si hay usuario y no está autenticado, autenticarlo
        if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = usuarioServicio.loadUserByUsername(nombreUsuario);

            if (jwtUtil.validarToken(token)) {
                // Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // La contraseña ya no es necesaria
                        userDetails.getAuthorities()
                );

                // Añadir detalles de la petición
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}