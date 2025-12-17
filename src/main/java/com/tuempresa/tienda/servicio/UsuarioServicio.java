package com.tuempresa.tienda.servicio;

import com.tuempresa.tienda.modelo.Usuario;
import com.tuempresa.tienda.repositorio.UsuarioRepositorio;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServicio implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // CORRECCIÓN: Llamamos a findByNombreUsuario
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> {
                    String nombreRol = rol.getNombre().name();
                    if (!nombreRol.startsWith("ROLE_")) {
                        nombreRol = "ROLE_" + nombreRol;
                    }
                    return new SimpleGrantedAuthority(nombreRol);
                })
                .collect(Collectors.toList());

        // Nota: usuario.getNombreUsuario() es el getter de tu entidad
        return new User(
                usuario.getNombreUsuario(),
                usuario.getPassword(),
                authorities
        );
    }

    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepositorio.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Usuario no encontrado con id: " + id);
        }
        usuarioRepositorio.deleteById(id);
    }
}
