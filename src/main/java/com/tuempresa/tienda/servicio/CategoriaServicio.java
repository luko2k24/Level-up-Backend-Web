package com.tuempresa.tienda.servicio;

import com.tuempresa.tienda.modelo.Categoria;

import java.util.List;

public interface CategoriaServicio {

    List<Categoria> listar();
    Categoria buscarPorId(Long id);
    Categoria crear(Categoria categoria);
    Categoria actualizar(Long id, Categoria categoria);
    void eliminar(Long id);
}
