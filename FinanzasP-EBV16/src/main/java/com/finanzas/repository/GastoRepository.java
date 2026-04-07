package com.finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finanzas.entity.Gasto;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findAllByOrderByFechaDesc();
}
