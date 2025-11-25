package com.tuempresa.tienda.controlador;

import com.tuempresa.tienda.modelo.Categoria;
import com.tuempresa.tienda.servicio.CategoriaServicio;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class categoriacontrolador {

    private final CategoriaServicio categoriaServicio;

    public categoriacontrolador(CategoriaServicio categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }

    //  CUALQUIER USUARIO PUEDE VER
    @GetMapping
    public List<Categoria> listar() {
        return categoriaServicio.listar();
    }

    @GetMapping("/{id}")
    public Categoria buscarPorId(@PathVariable Long id) {
        return categoriaServicio.buscarPorId(id);
    }

    // SOLO ADMIN PUEDE CREAR
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Categoria crear(@RequestBody Categoria categoria) {
        return categoriaServicio.crear(categoria);
    }

    //  SOLO ADMIN PUEDE ACTUALIZAR
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Categoria actualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
        return categoriaServicio.actualizar(id, categoria);
    }

    // SOLO ADMIN PUEDE ELIMINAR
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        categoriaServicio.eliminar(id);
    }
}
