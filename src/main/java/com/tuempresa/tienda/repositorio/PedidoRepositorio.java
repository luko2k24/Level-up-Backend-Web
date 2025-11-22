package com.tuempresa.tienda.repositorio;

import com.tuempresa.tienda.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {

}