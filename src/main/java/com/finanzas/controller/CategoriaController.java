package com.finanzas.controller;

import com.finanzas.dto.ActualizarCategoriaDTO;
import com.finanzas.dto.ConfirmarEliminacionDTO;
import com.finanzas.dto.ErrorResponseDTO;
import com.finanzas.dto.InfoEliminacionDTO;
import com.finanzas.entity.Categoria;
import com.finanzas.entity.Gasto;
import com.finanzas.entity.Ingreso;
import com.finanzas.entity.TipoCategoria;
import com.finanzas.entity.Usuario;
import com.finanzas.repository.CategoriaRepository;
import com.finanzas.repository.GastoRepository;
import com.finanzas.repository.IngresoRepository;
import com.finanzas.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private static final Pattern NOMBRE_CATEGORIA_PERMITIDO = Pattern.compile("^[\\p{L}\\p{N} _\\-()\\/]+$");

    private final CategoriaRepository categoriaRepository;
    private final GastoRepository gastoRepository;
    private final IngresoRepository ingresoRepository;
    private final UsuarioRepository usuarioRepository;

    public CategoriaController(CategoriaRepository categoriaRepository,
                               GastoRepository gastoRepository,
                               IngresoRepository ingresoRepository,
                               UsuarioRepository usuarioRepository) {
        this.categoriaRepository = categoriaRepository;
        this.gastoRepository = gastoRepository;
        this.ingresoRepository = ingresoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) Long usuarioId) {
        return listarPorTipo(TipoCategoria.GASTO, usuarioId);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> listarPorTipo(@PathVariable TipoCategoria tipo, @RequestParam(required = false) Long usuarioId) {
        if (usuarioId == null) {
            return ResponseEntity.ok(categoriaRepository.findByTipoAndUsuarioIsNullOrderByNombre(tipo));
        }
        if (!usuarioRepository.existsById(usuarioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Usuario no encontrado", "El usuario con ID " + usuarioId + " no existe"));
        }
        return ResponseEntity.ok(categoriaRepository.findVisiblesPorUsuario(tipo, usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoria no encontrada", "La categoria con ID " + id + " no existe"));
        }
        if (!esVisibleParaUsuario(categoria.get(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO(403, "Categoria no permitida", "La categoria no pertenece al usuario"));
        }
        return ResponseEntity.ok(categoria.get());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Categoria categoria) {
        if (categoria == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Categoria invalida", "Debe enviar los datos de la categoria"));
        }

        ResponseEntity<?> validacion = validarDatosCategoria(categoria.getNombre(), categoria.getTipo());
        if (validacion != null) {
            return validacion;
        }

        if (categoria.getUsuarioId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Usuario obligatorio", "Debe enviar usuarioId"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(categoria.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Usuario no encontrado", "El usuario con ID " + categoria.getUsuarioId() + " no existe"));
        }

        String nombre = categoria.getNombre().trim();
        if (existeNombreVisible(nombre, categoria.getTipo(), categoria.getUsuarioId(), null)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Categoria duplicada",
                            "Ya existe una categoria visible con el nombre '" + nombre + "'"));
        }

        categoria.setNombre(nombre);
        categoria.setUsuario(usuarioOpt.get());
        Categoria guardada = categoriaRepository.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestParam(required = false) Long usuarioId,
                                        @Valid @RequestBody ActualizarCategoriaDTO categoriaActualizada) {
        if (categoriaActualizada == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Categoria invalida", "Debe enviar los datos de la categoria"));
        }

        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoria no encontrada",
                            "No se puede actualizar. La categoria con ID " + id + " no existe"));
        }

        Categoria categoria = categoriaExistente.get();
        ResponseEntity<?> permiso = validarGestionCategoria(categoria, usuarioId, "modificar");
        if (permiso != null) return permiso;

        String nombre = categoriaActualizada.getNombre() != null ? categoriaActualizada.getNombre().trim() : "";
        TipoCategoria tipo = categoriaActualizada.getTipo() != null ? categoriaActualizada.getTipo() : TipoCategoria.GASTO;
        ResponseEntity<?> validacion = validarDatosCategoria(nombre, tipo);
        if (validacion != null) return validacion;

        if (existeNombreVisible(nombre, TipoCategoria.GASTO, usuarioId, id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Nombre duplicado",
                            "Ya existe otra categoria visible con el nombre '" + nombre + "'"));
        }

        categoria.setNombre(nombre);
        categoria.setDescripcion(categoriaActualizada.getDescripcion());
        categoria.setTipo(TipoCategoria.GASTO);
        return ResponseEntity.ok(categoriaRepository.save(categoria));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarParcial(@PathVariable Long id,
                                               @RequestParam(required = false) Long usuarioId,
                                               @RequestBody ActualizarCategoriaDTO actualizacion) {
        if (actualizacion == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Categoria invalida", "Debe enviar los datos de la categoria"));
        }

        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoria no encontrada", "La categoria con ID " + id + " no existe"));
        }

        Categoria categoria = categoriaExistente.get();
        ResponseEntity<?> permiso = validarGestionCategoria(categoria, usuarioId, "modificar");
        if (permiso != null) return permiso;

        if (actualizacion.getNombre() != null && !actualizacion.getNombre().trim().isEmpty()) {
            String nombre = actualizacion.getNombre().trim();
            if (!NOMBRE_CATEGORIA_PERMITIDO.matcher(nombre).matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO(400, "Nombre invalido",
                                "El nombre solo puede contener letras, numeros, espacios, -, _, (), /"));
            }
            if (existeNombreVisible(nombre, TipoCategoria.GASTO, usuarioId, id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponseDTO(409, "Nombre duplicado",
                                "Ya existe otra categoria visible con el nombre '" + nombre + "'"));
            }
            categoria.setNombre(nombre);
        }

        if (actualizacion.getDescripcion() != null) {
            categoria.setDescripcion(actualizacion.getDescripcion());
        }

        categoria.setTipo(TipoCategoria.GASTO);
        return ResponseEntity.ok(categoriaRepository.save(categoria));
    }

    @PatchMapping("/{id}/nombre")
    public ResponseEntity<?> actualizarNombre(@PathVariable Long id,
                                              @RequestParam(required = false) Long usuarioId,
                                              @RequestBody Map<String, String> request) {
        ActualizarCategoriaDTO dto = new ActualizarCategoriaDTO();
        dto.setNombre(request.get("nombre"));
        return actualizarParcial(id, usuarioId, dto);
    }

    @PatchMapping("/{id}/descripcion")
    public ResponseEntity<?> actualizarDescripcion(@PathVariable Long id,
                                                   @RequestParam(required = false) Long usuarioId,
                                                   @RequestBody Map<String, String> request) {
        ActualizarCategoriaDTO dto = new ActualizarCategoriaDTO();
        dto.setDescripcion(request.getOrDefault("descripcion", ""));
        return actualizarParcial(id, usuarioId, dto);
    }

    @GetMapping("/{id}/referencias")
    public ResponseEntity<?> obtenerInfoEliminacion(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoria no encontrada", "La categoria con ID " + id + " no existe"));
        }

        ResponseEntity<?> permiso = validarGestionCategoria(categoriaExistente.get(), usuarioId, "eliminar");
        if (permiso != null) return permiso;

        Categoria categoria = categoriaExistente.get();
        long gastosVinculados = gastoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id).size();
        long ingresosVinculados = ingresoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id).size();
        return ResponseEntity.ok(new InfoEliminacionDTO(id, categoria.getNombre(), gastosVinculados, ingresosVinculados));
    }

    @PostMapping("/{id}/eliminar")
    public ResponseEntity<?> eliminarConOpciones(@PathVariable Long id,
                                                 @RequestParam(required = false) Long usuarioId,
                                                 @RequestBody ConfirmarEliminacionDTO dto) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoria no encontrada", "La categoria con ID " + id + " no existe"));
        }

        Categoria categoria = categoriaExistente.get();
        ResponseEntity<?> permiso = validarGestionCategoria(categoria, usuarioId, "eliminar");
        if (permiso != null) return permiso;

        long gastosVinculados = gastoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id).size();
        long ingresosVinculados = ingresoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id).size();

        if (gastosVinculados == 0 && ingresosVinculados == 0) {
            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        if (dto == null || !dto.isConfirmar()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new InfoEliminacionDTO(id, categoria.getNombre(), gastosVinculados, ingresosVinculados));
        }

        Long reasignarId = dto.getReasignarCategoriaId();
        if (reasignarId != null) {
            if (reasignarId.equals(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO(400, "Operacion invalida", "La categoria de reasignacion no puede ser la misma"));
            }
            Optional<Categoria> targetOpt = categoriaRepository.findById(reasignarId);
            if (targetOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponseDTO(404, "Categoria de reasignacion no encontrada", "La categoria con ID " + reasignarId + " no existe"));
            }
            if (!esVisibleParaUsuario(targetOpt.get(), usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponseDTO(403, "Categoria no permitida", "La categoria de reasignacion no pertenece al usuario"));
            }

            Categoria target = targetOpt.get();
            List<Gasto> gastosList = gastoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id);
            gastosList.forEach(g -> g.setCategoria(target));
            gastoRepository.saveAll(gastosList);

            List<Ingreso> ingresosList = ingresoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id);
            ingresosList.forEach(i -> i.setCategoria(target));
            ingresoRepository.saveAll(ingresosList);

            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        if (dto.isEliminarTransacciones()) {
            gastoRepository.deleteAll(gastoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id));
            ingresoRepository.deleteAll(ingresoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id));
            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new InfoEliminacionDTO(id, categoria.getNombre(), gastosVinculados, ingresosVinculados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Categoria no encontrada", "La categoria con ID " + id + " no existe"));
        }

        Categoria categoria = categoriaExistente.get();
        ResponseEntity<?> permiso = validarGestionCategoria(categoria, usuarioId, "eliminar");
        if (permiso != null) return permiso;

        long gastosVinculados = gastoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id).size();
        long ingresosVinculados = ingresoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, id).size();
        if (gastosVinculados > 0 || ingresosVinculados > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Categoria en uso",
                            "No se puede eliminar. La categoria '" + categoria.getNombre() + "' tiene "
                                    + (gastosVinculados + ingresosVinculados) + " transacciones vinculadas."));
        }

        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> validarDatosCategoria(String nombre, TipoCategoria tipo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Nombre invalido", "El nombre es obligatorio"));
        }
        if (nombre.trim().length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Nombre invalido", "El nombre no puede superar 50 caracteres"));
        }
        if (!NOMBRE_CATEGORIA_PERMITIDO.matcher(nombre.trim()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Nombre invalido",
                            "El nombre solo puede contener letras, numeros, espacios, -, _, (), /"));
        }
        if (tipo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Tipo invalido", "El tipo de categoria es obligatorio"));
        }
        if (tipo != TipoCategoria.GASTO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Tipo invalido", "Solo se permiten categorias de tipo GASTO"));
        }
        return null;
    }

    private boolean existeNombreVisible(String nombre, TipoCategoria tipo, Long usuarioId, Long ignorarId) {
        return categoriaRepository.findVisiblesPorUsuario(tipo, usuarioId).stream()
                .anyMatch(c -> c.getNombre() != null
                        && c.getNombre().equalsIgnoreCase(nombre)
                        && (ignorarId == null || !c.getId().equals(ignorarId)));
    }

    private ResponseEntity<?> validarGestionCategoria(Categoria categoria, Long usuarioId, String accion) {
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Usuario obligatorio", "Debe enviar usuarioId"));
        }
        if (categoria.getUsuario() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO(403, "Categoria predefinida",
                            "Las categorias predefinidas no se pueden " + accion));
        }
        if (!categoria.getUsuario().getId().equals(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO(403, "Categoria no permitida", "La categoria no pertenece al usuario"));
        }
        return null;
    }

    private boolean esVisibleParaUsuario(Categoria categoria, Long usuarioId) {
        return categoria.getUsuario() == null
                || (usuarioId != null && categoria.getUsuario().getId().equals(usuarioId));
    }
}
