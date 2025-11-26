package com.tuempresa.tienda.controlador;

import com.tuempresa.tienda.modelo.Producto;
import com.tuempresa.tienda.servicio.ProductoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "http://localhost:5173")// 🛑 CORRECCIÓN CLAVE: La ruta base es ahora /api/v1/productos
public class    ProductoControlador {

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    // --- ENDPOINTS PÚBLICOS (permitAll) ---
    // GET /api/v1/productos
    @GetMapping // La ruta final es /api/v1/productos (gracias a la anotación de la clase)
    public List<Producto> listarProductos() {
        return productoServicio.obtenerTodos();
    }

    // GET /api/v1/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return productoServicio.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- ENDPOINTS ADMINISTRATIVOS (hasRole("ADMIN")) ---

    // POST /api/v1/productos/admin
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoServicio.crearProducto(producto);
            return ResponseEntity.status(201).body(nuevoProducto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/v1/productos/admin/{id}
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoDetalles) {
        try {
            Producto actualizado = productoServicio.actualizarProducto(id, productoDetalles);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/v1/productos/admin/{id}
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            productoServicio.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}