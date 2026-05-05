package com.finanzas.controller;

import com.finanzas.entity.Categoria;
import com.finanzas.entity.TipoCategoria;
import com.finanzas.entity.Gasto;
import com.finanzas.entity.Ingreso;
import com.finanzas.repository.CategoriaRepository;
import com.finanzas.repository.GastoRepository;
import com.finanzas.repository.IngresoRepository;
import com.finanzas.dto.ActualizarCategoriaDTO;
import com.finanzas.dto.ConfirmarEliminacionDTO;
import com.finanzas.dto.ErrorResponseDTO;
import com.finanzas.dto.InfoEliminacionDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final GastoRepository gastoRepository;
    private final IngresoRepository ingresoRepository;

    public CategoriaController(CategoriaRepository categoriaRepository,
                             GastoRepository gastoRepository,
                             IngresoRepository ingresoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.gastoRepository = gastoRepository;
        this.ingresoRepository = ingresoRepository;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @GetMapping("/tipo/{tipo}")
    public List<Categoria> listarPorTipo(@PathVariable TipoCategoria tipo) {
        return categoriaRepository.findByTipoOrderByNombre(tipo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoría no encontrada", "La categoría con ID " + id + " no existe"));
        }
        return ResponseEntity.ok(categoria.get());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Categoria categoria) {
        // Validar que no exista una categoría con el mismo nombre
        if (categoriaRepository.findByNombre(categoria.getNombre()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Nombre duplicado", 
                            "Ya existe una categoría con el nombre '" + categoria.getNombre() + "'"));
        }
        Categoria guardada = categoriaRepository.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, 
                                        @Valid @RequestBody ActualizarCategoriaDTO categoriaActualizada) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoría no encontrada", 
                            "No se puede actualizar. La categoría con ID " + id + " no existe"));
        }

        // Validar que no exista otra categoría con el mismo nombre
        Optional<Categoria> otraCategoriaConNombre = categoriaRepository.findByNombre(categoriaActualizada.getNombre());
        if (otraCategoriaConNombre.isPresent() && !otraCategoriaConNombre.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Nombre duplicado", 
                            "Ya existe otra categoría con el nombre '" + categoriaActualizada.getNombre() + "'"));
        }

        Categoria categoria = categoriaExistente.get();
        categoria.setNombre(categoriaActualizada.getNombre());
        categoria.setDescripcion(categoriaActualizada.getDescripcion());
        categoria.setTipo(categoriaActualizada.getTipo());

        Categoria actualizada = categoriaRepository.save(categoria);
        return ResponseEntity.ok(actualizada);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarParcial(@PathVariable Long id, 
                                               @RequestBody ActualizarCategoriaDTO actualizacion) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoría no encontrada", 
                            "La categoría con ID " + id + " no existe"));
        }

        Categoria categoria = categoriaExistente.get();

        // Actualizar solo los campos que vienen en la solicitud
        if (actualizacion.getNombre() != null && !actualizacion.getNombre().isEmpty()) {
            // Validar que no exista otra categoría con el nuevo nombre
            Optional<Categoria> otraCategoriaConNombre = categoriaRepository.findByNombre(actualizacion.getNombre());
            if (otraCategoriaConNombre.isPresent() && !otraCategoriaConNombre.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponseDTO(409, "Nombre duplicado", 
                                "Ya existe otra categoría con el nombre '" + actualizacion.getNombre() + "'"));
            }
            categoria.setNombre(actualizacion.getNombre());
        }

        if (actualizacion.getDescripcion() != null) {
            categoria.setDescripcion(actualizacion.getDescripcion());
        }

        if (actualizacion.getTipo() != null) {
            categoria.setTipo(actualizacion.getTipo());
        }

        Categoria actualizada = categoriaRepository.save(categoria);
        return ResponseEntity.ok(actualizada);
    }

    @PatchMapping("/{id}/nombre")
    public ResponseEntity<?> actualizarNombre(@PathVariable Long id, 
                                              @RequestBody Map<String, String> request) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoría no encontrada", 
                            "La categoría con ID " + id + " no existe"));
        }

        String nuevoNombre = request.get("nombre");
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Nombre inválido", 
                            "El nombre no puede estar vacío"));
        }

        // Validar que no exista otra categoría con el nuevo nombre
        Optional<Categoria> otraCategoriaConNombre = categoriaRepository.findByNombre(nuevoNombre);
        if (otraCategoriaConNombre.isPresent() && !otraCategoriaConNombre.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Nombre duplicado", 
                            "Ya existe otra categoría con el nombre '" + nuevoNombre + "'"));
        }

        Categoria categoria = categoriaExistente.get();
        categoria.setNombre(nuevoNombre);
        Categoria actualizada = categoriaRepository.save(categoria);
        return ResponseEntity.ok(actualizada);
    }

    @PatchMapping("/{id}/descripcion")
    public ResponseEntity<?> actualizarDescripcion(@PathVariable Long id, 
                                                   @RequestBody Map<String, String> request) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoría no encontrada", 
                            "La categoría con ID " + id + " no existe"));
        }

        String nuevaDescripcion = request.get("descripcion");
        Categoria categoria = categoriaExistente.get();
        categoria.setDescripcion(nuevaDescripcion != null ? nuevaDescripcion : "");
        
        Categoria actualizada = categoriaRepository.save(categoria);
        return ResponseEntity.ok(actualizada);
    }

    @GetMapping("/{id}/referencias")
    public ResponseEntity<?> obtenerInfoEliminacion(@PathVariable Long id) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoría no encontrada", 
                            "La categoría con ID " + id + " no existe"));
        }

        Categoria categoria = categoriaExistente.get();
        long gastosVinculados = gastoRepository.countByCategoria_Id(id);
        long ingresosVinculados = ingresoRepository.countByCategoria_Id(id);

        InfoEliminacionDTO info = new InfoEliminacionDTO(
            id,
            categoria.getNombre(),
            gastosVinculados,
            ingresosVinculados
        );

        return ResponseEntity.ok(info);
    }
    @PostMapping("/{id}/eliminar")
    public ResponseEntity<?> eliminarConOpciones(@PathVariable Long id, @RequestBody ConfirmarEliminacionDTO dto) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);

        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoría no encontrada", 
                            "La categoría con ID " + id + " no existe"));
        }

        Categoria categoria = categoriaExistente.get();
        long gastosVinculados = gastoRepository.countByCategoria_Id(id);
        long ingresosVinculados = ingresoRepository.countByCategoria_Id(id);

        // Si no hay referencias, eliminar directamente
        if (gastosVinculados == 0 && ingresosVinculados == 0) {
            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        // Si no se confirma la eliminación, devolver info sobre referencias
        if (dto == null || !dto.isConfirmar()) {
            InfoEliminacionDTO info = new InfoEliminacionDTO(id, categoria.getNombre(), gastosVinculados, ingresosVinculados);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(info);
        }

        Long reasignarId = dto.getReasignarCategoriaId();
        boolean eliminarTrans = dto.isEliminarTransacciones();

        // Reasignar transacciones a otra categoría
        if (reasignarId != null) {
            if (reasignarId.equals(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO(400, "Operación inválida", "La categoría de reasignación no puede ser la misma que la categoría a eliminar"));
            }
            Optional<Categoria> targetOpt = categoriaRepository.findById(reasignarId);
            if (targetOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponseDTO(404, "Categoría de reasignación no encontrada", "La categoría con ID " + reasignarId + " no existe"));
            }
            Categoria target = targetOpt.get();

            List<Gasto> gastosList = gastoRepository.findByCategoria_IdOrderByFechaDesc(id);
            if (!gastosList.isEmpty()) {
                gastosList.forEach(g -> g.setCategoria(target));
                gastoRepository.saveAll(gastosList);
            }

            List<Ingreso> ingresosList = ingresoRepository.findByCategoria_IdOrderByFechaDesc(id);
            if (!ingresosList.isEmpty()) {
                ingresosList.forEach(i -> i.setCategoria(target));
                ingresoRepository.saveAll(ingresosList);
            }

            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        // Eliminar transacciones vinculadas junto con la categoría
        if (eliminarTrans) {
            List<Gasto> gastosList = gastoRepository.findByCategoria_IdOrderByFechaDesc(id);
            if (!gastosList.isEmpty()) gastoRepository.deleteAll(gastosList);

            List<Ingreso> ingresosList = ingresoRepository.findByCategoria_IdOrderByFechaDesc(id);
            if (!ingresosList.isEmpty()) ingresoRepository.deleteAll(ingresosList);

            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        // Por defecto, devolver información sobre referencias
        InfoEliminacionDTO info = new InfoEliminacionDTO(id, categoria.getNombre(), gastosVinculados, ingresosVinculados);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(info);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoría no encontrada", 
                            "La categoría con ID " + id + " no existe"));
        }

        Categoria categoria = categoriaExistente.get();
        long gastosVinculados = gastoRepository.countByCategoria_Id(id);
        long ingresosVinculados = ingresoRepository.countByCategoria_Id(id);

        // Validar integridad referencial
        if (gastosVinculados > 0 || ingresosVinculados > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Categoría en uso",
                            "No se puede eliminar. La categoría '" + categoria.getNombre() + 
                            "' tiene " + (gastosVinculados + ingresosVinculados) + 
                            " transacciones vinculadas. Reasigne o elimine las transacciones primero."));
        }

        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
