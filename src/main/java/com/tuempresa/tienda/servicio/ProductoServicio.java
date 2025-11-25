package com.tuempresa.tienda.servicio;

import com.tuempresa.tienda.modelo.Categoria;
import com.tuempresa.tienda.modelo.Producto;
import com.tuempresa.tienda.repositorio.ProductoRepositorio;
import com.tuempresa.tienda.repositorio.CategoriaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;

    public ProductoServicio(ProductoRepositorio productoRepositorio, CategoriaRepositorio categoriaRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.categoriaRepositorio = categoriaRepositorio;
    }

    // --- MÉTODOS PÚBLICOS (Lectura) ---

    @Transactional(readOnly = true)
    public List<Producto> obtenerTodos() {
        return productoRepositorio.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepositorio.findById(id);
    }

    // --- MÉTODOS ADMINISTRATIVOS (Escritura) ---

    @Transactional
    public Producto crearProducto(Producto producto) {
        // Lógica de validación: Asegurar que la categoría exista antes de guardar
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            throw new RuntimeException("Se requiere una categoría válida para el producto.");
        }

        Categoria categoria = categoriaRepositorio.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new RuntimeException("La categoría no existe."));

        producto.setCategoria(categoria);
        return productoRepositorio.save(producto);
    }

    @Transactional
    public Producto actualizarProducto(Long id, Producto productoDetalles) {
        return productoRepositorio.findById(id).map(producto -> {
            producto.setNombre(productoDetalles.getNombre());
            producto.setDescripcion(productoDetalles.getDescripcion());
            producto.setPrecio(productoDetalles.getPrecio());

            // Actualizar categoría (debe existir)
            if (productoDetalles.getCategoria() != null && productoDetalles.getCategoria().getId() != null) {
                Categoria categoria = categoriaRepositorio.findById(productoDetalles.getCategoria().getId())
                        .orElseThrow(() -> new RuntimeException("La categoría para actualizar no existe."));
                producto.setCategoria(categoria);
            }

            return productoRepositorio.save(producto);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + id));
    }

    public void eliminarProducto(Long id) {
        if (!productoRepositorio.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id " + id);
        }
        productoRepositorio.deleteById(id);
    }
}