package com.finanzas.repository;

import com.finanzas.entity.FuenteIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuenteIngresoRepository extends JpaRepository<FuenteIngreso, Long> {
    Optional<FuenteIngreso> findByNombre(String nombre);
    List<FuenteIngreso> findAllByOrderByNombre();
    List<FuenteIngreso> findByUsuarioIsNullOrderByNombre();
    List<FuenteIngreso> findByUsuario_IdOrderByNombre(Long usuarioId);

    @Query("select f from FuenteIngreso f where f.usuario is null or f.usuario.id = ?1 order by f.nombre")
    List<FuenteIngreso> findVisiblesPorUsuario(Long usuarioId);
    long countByNombre(String nombre);
}
