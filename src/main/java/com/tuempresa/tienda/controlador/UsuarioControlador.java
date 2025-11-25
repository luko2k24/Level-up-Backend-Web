package com.tuempresa.tienda.controlador;

import com.tuempresa.tienda.modelo.Usuario;
import com.tuempresa.tienda.repositorio.UsuarioRepositorio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioControlador {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioControlador(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    // 📌 1. LISTAR USUARIOS (GET /api/v1/usuarios)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    // 📌 2. ELIMINAR USUARIO (DELETE /api/v1/usuarios/{id})
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {

        // Verificamos si el usuario existe antes de intentar eliminarlo
        if (!usuarioRepositorio.existsById(id)) {
            // Si no existe, devolvemos 404 Not Found
            return ResponseEntity.notFound().build();
        }

        usuarioRepositorio.deleteById(id);

        // Devolvemos 204 No Content para indicar éxito sin cuerpo de respuesta
        return ResponseEntity.noContent().build();
    }
}