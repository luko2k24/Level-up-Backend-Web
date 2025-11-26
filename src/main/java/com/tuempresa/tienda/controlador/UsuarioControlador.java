package com.tuempresa.tienda.controlador;

import com.tuempresa.tienda.modelo.Usuario;
import com.tuempresa.tienda.servicio.UsuarioServicio; // Usamos el Servicio, no el Repositorio directo
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    // Inyección del Servicio
    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    // 1. Listar Usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioServicio.listarTodos();

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build(); // Devuelve 204 si no hay nadie (raro si eres admin)
        }

        return ResponseEntity.ok(usuarios);
    }

    // 2. Eliminar Usuario
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioServicio.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Si el usuario no existe o hay error lógico
            return ResponseEntity.notFound().build();
        }
    }
}