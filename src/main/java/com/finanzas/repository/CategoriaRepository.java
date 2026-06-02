package com.finanzas.repository;

import com.finanzas.entity.Categoria;
import com.finanzas.entity.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
    List<Categoria> findByTipo(TipoCategoria tipo);
    List<Categoria> findByTipoOrderByNombre(TipoCategoria tipo);
    List<Categoria> findByTipoAndUsuarioIsNullOrderByNombre(TipoCategoria tipo);
    List<Categoria> findByTipoAndUsuario_IdOrderByNombre(TipoCategoria tipo, Long usuarioId);

    @Query("select c from Categoria c where c.tipo = ?1 and (c.usuario is null or c.usuario.id = ?2) order by c.nombre")
    List<Categoria> findVisiblesPorUsuario(TipoCategoria tipo, Long usuarioId);
}
