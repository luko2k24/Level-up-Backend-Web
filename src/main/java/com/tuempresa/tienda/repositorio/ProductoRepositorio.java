package com.tuempresa.tienda.repositorio;


import com.tuempresa.tienda.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

}