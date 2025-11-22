package com.tuempresa.tienda.repositorio;

import com.tuempresa.tienda.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {

}