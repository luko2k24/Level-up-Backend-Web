package com.tuempresa.tienda.repositorio;


import com.tuempresa.tienda.modelo.ERol;
import com.tuempresa.tienda.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Para buscar roles por nombre (ej. ROLE_ADMIN)
public interface RolRepositorio extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(ERol nombre);
}