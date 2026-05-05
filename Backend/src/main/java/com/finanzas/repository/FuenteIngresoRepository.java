package com.finanzas.repository;

import com.finanzas.entity.FuenteIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuenteIngresoRepository extends JpaRepository<FuenteIngreso, Long> {
    Optional<FuenteIngreso> findByNombre(String nombre);
    List<FuenteIngreso> findAllByOrderByNombre();
    long countByNombre(String nombre);
}
