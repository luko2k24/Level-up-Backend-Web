package com.tuempresa.tienda.controlador;

import com.tuempresa.tienda.modelo.Pedido;
import com.tuempresa.tienda.servicio.PedidoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoControlador {

    private final PedidoServicio pedidoServicio;

    public PedidoControlador(PedidoServicio pedidoServicio) {
        this.pedidoServicio = pedidoServicio;
    }

    // --- ENDPOINTS PARA VISUALIZACIÓN (ADMIN/VENDEDOR) ---
    // Requieren hasAnyRole('ADMIN', 'VENDEDOR')

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public List<Pedido> listarPedidos() {
        return pedidoServicio.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable Long id) {
        return pedidoServicio.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- ENDPOINT TRANSACCIONAL (CLIENTE/USUARIO) ---

    @PostMapping
    @PreAuthorize("isAuthenticated()") // Requiere que el usuario esté logueado (incluye CLIENTE)
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido nuevoPedido, Authentication authentication) {

        String nombreUsuario = authentication.getName();

        try {
            Pedido pedidoCreado = pedidoServicio.crearPedido(nuevoPedido, nombreUsuario);
            return ResponseEntity.status(201).body(pedidoCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}