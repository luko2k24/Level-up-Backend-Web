package com.tuempresa.tienda.servicio;

import com.tuempresa.tienda.modelo.Pedido;
import com.tuempresa.tienda.modelo.Producto;
import com.tuempresa.tienda.modelo.Usuario;
import com.tuempresa.tienda.repositorio.PedidoRepositorio;
import com.tuempresa.tienda.repositorio.ProductoRepositorio;
import com.tuempresa.tienda.repositorio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoServicio {

    private final PedidoRepositorio pedidoRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public PedidoServicio(PedidoRepositorio pedidoRepositorio,
                          ProductoRepositorio productoRepositorio,
                          UsuarioRepositorio usuarioRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    // 🛑 MÉTODO CORREGIDO: Inicialización Forzada para evitar ERR_INCOMPLETE_CHUNKED_ENCODING
    @Transactional
    public Pedido crearPedido(Pedido nuevoPedido, String nombreUsuario) {

        // 1. Obtener el usuario (el cliente) por el nombre de usuario del JWT
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Error de sesión: Usuario no encontrado para el pedido."));
        nuevoPedido.setUsuario(usuario);

        // 2. Iterar y validar cada ítem del pedido (DETALLE_BOLETA)
        for (com.tuempresa.tienda.modelo.ItemPedido item : nuevoPedido.getItems()) {
            Producto producto = productoRepositorio.findById(item.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Error: Producto no encontrado con ID " + item.getProducto().getId()));

            // Asignar el pedido al detalle y asegurar el precio unitario correcto
            item.setPedido(nuevoPedido);
            item.setPrecioUnitario(producto.getPrecio());
        }

        // 3. Guardar la BOLETA y sus detalles
        Pedido pedidoCreado = pedidoRepositorio.save(nuevoPedido);

        // 🛑 FIX: Inicialización Forzada de Entidades LAZY antes de cerrar la transacción
        if (pedidoCreado.getUsuario() != null) {
            // Inicializa el proxy del Usuario:
            pedidoCreado.getUsuario().getNombreUsuario();
        }
        // Inicializa la lista de ítems:
        pedidoCreado.getItems().size();

        return pedidoCreado; // <-- Ahora este objeto se serializará sin error
    }

    // Métodos para el VENDEDOR/ADMIN (visualizar órdenes)
    public List<Pedido> obtenerTodos() {
        return pedidoRepositorio.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepositorio.findById(id);
    }
}