package com.finanzas.repository;

import com.finanzas.entity.Categoria;
import com.finanzas.entity.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByTipo(TipoCategoria tipo);
    List<Categoria> findByTipoOrderByNombre(TipoCategoria tipo);
    List<Categoria> findByTipoAndUsuario_IdOrderByNombre(TipoCategoria tipo, Long usuarioId);

    @Query("select c from Categoria c where c.tipo = ?1 and c.usuario.id = ?2 order by c.nombre")
    List<Categoria> findVisiblesPorUsuario(TipoCategoria tipo, Long usuarioId);
}
