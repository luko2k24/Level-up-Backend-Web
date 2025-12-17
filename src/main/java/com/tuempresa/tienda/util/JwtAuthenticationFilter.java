package com.tuempresa.tienda.util;

import com.tuempresa.tienda.servicio.UsuarioServicio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UsuarioServicio usuarioServicio;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UsuarioServicio usuarioServicio) {
        this.jwtUtil = jwtUtil;
        this.usuarioServicio = usuarioServicio;
    }

    // ==================================================
    // 🔓 ENDPOINTS QUE NO USAN JWT
    // ==================================================
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // IMPORTANTE: No excluir /api/v1/productos si es una ruta protegida (POST, DELETE, o rutas /admin)
        // Solo excluimos rutas de autenticación y quizás GET públicos específicos.

        if (path.startsWith("/api/v1/auth")) {
            return true;
        }

        // Ejemplo: Permitir ver productos sin login, pero requerir login para crear/borrar
        if (path.startsWith("/api/v1/productos") && method.equals("GET") && !path.contains("/admin")) {
            return true;
        }

        // Si tienes un endpoint específico público
        if (path.equals("/api/v1/pedidos/publico")) {
            return true;
        }

        return false;
    }

    // ==================================================
    // 🔒 FILTRO JWT NORMAL
    // ==================================================
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            // Si no hay token y no fue excluido por shouldNotFilter, dejamos pasar.
            // Spring Security se encargará de rechazar si la ruta requiere autenticación.
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String nombreUsuario;

        try {
            nombreUsuario = jwtUtil.obtenerNombreUsuario(token);
        } catch (Exception e) {
            logger.error("Error al obtener usuario del token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar usuario y roles desde la base de datos
            UserDetails userDetails = usuarioServicio.loadUserByUsername(nombreUsuario);

            if (jwtUtil.validarToken(token)) {

                // LOG TEMPORAL PARA DEBUG
                logger.info("Autenticando usuario: {}", nombreUsuario);
                logger.info("Roles cargados: {}", userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities() // Aquí pasamos los roles reales
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
