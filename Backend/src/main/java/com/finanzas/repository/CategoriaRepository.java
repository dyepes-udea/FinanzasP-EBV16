package com.finanzas.repository;

import com.finanzas.entity.Categoria;
import com.finanzas.entity.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
    List<Categoria> findByTipo(TipoCategoria tipo);
    List<Categoria> findByTipoOrderByNombre(TipoCategoria tipo);
}
