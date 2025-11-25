package com.tuempresa.tienda.servicio;

import com.tuempresa.tienda.modelo.Categoria;
import com.tuempresa.tienda.repositorio.CategoriaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServicioImpl implements CategoriaServicio {

    private final CategoriaRepositorio categoriaRepositorio;

    public CategoriaServicioImpl(CategoriaRepositorio categoriaRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
    }

    @Override
    public List<Categoria> listar() {
        return categoriaRepositorio.findAll();
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return categoriaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    @Override
    public Categoria crear(Categoria categoria) {
        return categoriaRepositorio.save(categoria);
    }

    @Override
    public Categoria actualizar(Long id, Categoria categoria) {
        Categoria existente = buscarPorId(id);
        existente.setNombre(categoria.getNombre());
        return categoriaRepositorio.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        categoriaRepositorio.deleteById(id);
    }
}
