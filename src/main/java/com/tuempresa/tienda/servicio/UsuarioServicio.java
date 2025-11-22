package com.tuempresa.tienda.servicio;


import com.tuempresa.tienda.modelo.Usuario;
import com.tuempresa.tienda.repositorio.UsuarioRepositorio;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServicio implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    // Método principal que Spring Security usa para el login
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con nombre: " + nombreUsuario));

        // Mapea los roles (ej. ROLE_ADMIN) a las autoridades de Spring Security
        Set<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map((rol) -> new SimpleGrantedAuthority(rol.getNombre().name()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                usuario.getNombreUsuario(),
                usuario.getPassword(), // Contraseña encriptada (BCrypt)
                authorities
        );
    }
}