package com.tuempresa.tienda.repositorio;

import com.tuempresa.tienda.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    // CORRECCIÓN: Usamos 'NombreUsuario' porque así se llama el campo en la entidad
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByEmail(String email);
}
