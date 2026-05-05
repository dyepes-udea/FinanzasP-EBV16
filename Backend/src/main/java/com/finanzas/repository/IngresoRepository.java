package com.finanzas.repository;

import com.finanzas.entity.Ingreso;
import com.finanzas.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findAllByOrderByFechaDesc();
    List<Ingreso> findByCategoriaOrderByFechaDesc(Categoria categoria);
    List<Ingreso> findByCategoria_IdOrderByFechaDesc(Long categoriaId);
    long countByCategoria_Id(Long categoriaId);
}
