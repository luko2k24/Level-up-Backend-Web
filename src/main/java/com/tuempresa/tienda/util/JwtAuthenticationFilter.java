package com.tuempresa.tienda.util;

import com.tuempresa.tienda.servicio.UsuarioServicio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioServicio usuarioServicio;


    public JwtAuthenticationFilter(JwtUtil jwtUtil, UsuarioServicio usuarioServicio) {
        this.jwtUtil = jwtUtil;
        this.usuarioServicio = usuarioServicio;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        String nombreUsuario = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);

            try {
                nombreUsuario = jwtUtil.obtenerNombreUsuario(token);
            } catch (Exception e) {
                // Token inválido, no bloquear, continuar
            }
        }

        if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = usuarioServicio.loadUserByUsername(nombreUsuario);

            if (jwtUtil.validarToken(token)) {

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
