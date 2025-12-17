package com.tuempresa.tienda.servicio;

import com.tuempresa.tienda.modelo.ItemPedido;
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

    public PedidoServicio(
            PedidoRepositorio pedidoRepositorio,
            ProductoRepositorio productoRepositorio,
            UsuarioRepositorio usuarioRepositorio
    ) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Transactional
    public Pedido crearPedido(Pedido nuevoPedido, String nombreUsuario) {

        // CORRECCIÓN: Usamos findByNombreUsuario
        Usuario usuario = usuarioRepositorio
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        nuevoPedido.setUsuario(usuario);
        nuevoPedido.setEstado("REGISTRADO");

        if (nuevoPedido.getItems() == null || nuevoPedido.getItems().isEmpty()) {
            throw new RuntimeException("El pedido no tiene items");
        }

        for (ItemPedido item : nuevoPedido.getItems()) {
            if (item.getProducto() == null || item.getProducto().getId() == null) {
                throw new RuntimeException("Item sin producto válido");
            }
            Producto producto = productoRepositorio
                    .findById(item.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            item.setPedido(nuevoPedido);
            item.setProducto(producto);
            item.setPrecioUnitario(producto.getPrecio());
        }

        return pedidoRepositorio.save(nuevoPedido);
    }

    @Transactional
    public Pedido crearPedidoPublico(Pedido nuevoPedido) {
        nuevoPedido.setEstado("REGISTRADO");

        if (nuevoPedido.getItems() == null || nuevoPedido.getItems().isEmpty()) {
            throw new RuntimeException("El pedido no tiene items");
        }

        for (ItemPedido item : nuevoPedido.getItems()) {
            if (item.getProducto() == null || item.getProducto().getId() == null) {
                throw new RuntimeException("Item sin producto válido");
            }
            Producto producto = productoRepositorio
                    .findById(item.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            item.setPedido(nuevoPedido);
            item.setProducto(producto);
            item.setPrecioUnitario(producto.getPrecio());
        }
        return pedidoRepositorio.save(nuevoPedido);
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepositorio.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepositorio.findById(id);
    }
}
