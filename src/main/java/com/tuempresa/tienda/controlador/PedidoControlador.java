package com.tuempresa.tienda.controlador;

import com.tuempresa.tienda.modelo.Pedido;
import com.tuempresa.tienda.servicio.PedidoServicio;
import org.springframework.http.HttpStatus;
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

    // ===============================
    // ENDPOINTS ADMIN / VENDEDOR
    // ===============================

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
                .orElse(ResponseEntity.notFound().build());
    }

    // ===============================
    // ENDPOINT PRIVADO (USUARIO LOGUEADO)
    // ===============================

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Pedido> crearPedido(
            @RequestBody Pedido nuevoPedido,
            Authentication authentication) {

        String nombreUsuario = authentication.getName();
        Pedido pedidoCreado = pedidoServicio.crearPedido(nuevoPedido, nombreUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCreado);
    }

    // ===============================
    // ✅ ENDPOINT PÚBLICO (CHECKOUT)
    // ===============================

    @PostMapping("/publico")
    public ResponseEntity<Pedido> crearPedidoPublico(@RequestBody Pedido nuevoPedido) {
        Pedido pedidoCreado = pedidoServicio.crearPedidoPublico(nuevoPedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCreado);
    }
}
