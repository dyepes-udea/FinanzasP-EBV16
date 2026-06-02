package com.finanzas.repository;

import com.finanzas.entity.Gasto;
import com.finanzas.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findAllByOrderByFechaDesc();
    List<Gasto> findByUsuario_IdOrderByFechaDesc(Long usuarioId);
    List<Gasto> findByCategoriaOrderByFechaDesc(Categoria categoria);
    List<Gasto> findByCategoria_IdOrderByFechaDesc(Long categoriaId);
    List<Gasto> findByUsuario_IdAndCategoria_IdOrderByFechaDesc(Long usuarioId, Long categoriaId);
    long countByCategoria_Id(Long categoriaId);
}
