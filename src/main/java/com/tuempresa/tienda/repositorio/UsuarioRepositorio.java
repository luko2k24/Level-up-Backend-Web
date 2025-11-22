package com.tuempresa.tienda.repositorio;


import com.tuempresa.tienda.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Esencial para la autenticación
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Boolean existsByNombreUsuario(String nombreUsuario);
    Boolean existsByEmail(String email);
}