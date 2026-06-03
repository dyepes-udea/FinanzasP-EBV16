package com.finanzas.controller;

import com.finanzas.dto.ActualizarFuenteIngresoDTO;
import com.finanzas.dto.ErrorResponseDTO;
import com.finanzas.dto.InfoEliminacionFuenteDTO;
import com.finanzas.entity.FuenteIngreso;
import com.finanzas.entity.Usuario;
import com.finanzas.repository.FuenteIngresoRepository;
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

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/fuentes-ingreso")
@CrossOrigin(origins = "*")
public class FuenteIngresoController {

    private static final Pattern NOMBRE_FUENTE_PERMITIDO = Pattern.compile("^[\\p{L}\\p{N} _\\-()\\/]+$");

    private final FuenteIngresoRepository fuenteRepository;
    private final IngresoRepository ingresoRepository;
    private final UsuarioRepository usuarioRepository;

    public FuenteIngresoController(FuenteIngresoRepository fuenteRepository,
                                   IngresoRepository ingresoRepository,
                                   UsuarioRepository usuarioRepository) {
        this.fuenteRepository = fuenteRepository;
        this.ingresoRepository = ingresoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) Long usuarioId) {
        if (usuarioId == null) {
            return ResponseEntity.ok(fuenteRepository.findByUsuarioIsNullOrderByNombre());
        }
        if (!usuarioRepository.existsById(usuarioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Usuario no encontrado", "El usuario con ID " + usuarioId + " no existe"));
        }
        return ResponseEntity.ok(fuenteRepository.findVisiblesPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        Optional<FuenteIngreso> fuente = fuenteRepository.findById(id);
        if (fuente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", "La fuente con ID " + id + " no existe"));
        }
        if (!esVisibleParaUsuario(fuente.get(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO(403, "Fuente no permitida", "La fuente no pertenece al usuario"));
        }
        return ResponseEntity.ok(fuente.get());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody FuenteIngreso fuente) {
        if (fuente == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Fuente invalida", "Debe enviar los datos de la fuente de ingreso"));
        }
        if (fuente.getUsuarioId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Usuario obligatorio", "Debe enviar usuarioId"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(fuente.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Usuario no encontrado", "El usuario con ID " + fuente.getUsuarioId() + " no existe"));
        }

        String nombre = fuente.getNombre() != null ? fuente.getNombre().trim() : "";
        ResponseEntity<?> validacion = validarNombre(nombre);
        if (validacion != null) return validacion;

        if (existeNombreVisible(nombre, fuente.getUsuarioId(), null)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Fuente duplicada",
                            "Ya existe una fuente visible con el nombre '" + nombre + "'"));
        }

        fuente.setNombre(nombre);
        fuente.setTipo("INGRESO");
        fuente.setUsuario(usuarioOpt.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(fuenteRepository.save(fuente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestParam(required = false) Long usuarioId,
                                        @Valid @RequestBody ActualizarFuenteIngresoDTO actualizar) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", "La fuente con ID " + id + " no existe"));
        }

        FuenteIngreso fuente = fuenteExistente.get();
        ResponseEntity<?> permiso = validarGestionFuente(fuente, usuarioId, "modificar");
        if (permiso != null) return permiso;

        String nombre = actualizar.getNombre() != null ? actualizar.getNombre().trim() : "";
        ResponseEntity<?> validacion = validarNombre(nombre);
        if (validacion != null) return validacion;

        if (existeNombreVisible(nombre, usuarioId, id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Nombre duplicado",
                            "Ya existe otra fuente visible con el nombre '" + nombre + "'"));
        }

        fuente.setNombre(nombre);
        fuente.setDescripcion(actualizar.getDescripcion());
        return ResponseEntity.ok(fuenteRepository.save(fuente));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarParcial(@PathVariable Long id,
                                               @RequestParam(required = false) Long usuarioId,
                                               @RequestBody ActualizarFuenteIngresoDTO actualizar) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", "La fuente con ID " + id + " no existe"));
        }

        FuenteIngreso fuente = fuenteExistente.get();
        ResponseEntity<?> permiso = validarGestionFuente(fuente, usuarioId, "modificar");
        if (permiso != null) return permiso;

        if (actualizar.getNombre() != null && !actualizar.getNombre().trim().isEmpty()) {
            String nombre = actualizar.getNombre().trim();
            ResponseEntity<?> validacion = validarNombre(nombre);
            if (validacion != null) return validacion;
            if (existeNombreVisible(nombre, usuarioId, id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponseDTO(409, "Nombre duplicado",
                                "Ya existe otra fuente visible con el nombre '" + nombre + "'"));
            }
            fuente.setNombre(nombre);
        }

        if (actualizar.getDescripcion() != null) {
            fuente.setDescripcion(actualizar.getDescripcion());
        }

        return ResponseEntity.ok(fuenteRepository.save(fuente));
    }

    @PatchMapping("/{id}/nombre")
    public ResponseEntity<?> actualizarNombre(@PathVariable Long id,
                                              @RequestParam(required = false) Long usuarioId,
                                              @RequestBody Map<String, String> request) {
        ActualizarFuenteIngresoDTO dto = new ActualizarFuenteIngresoDTO();
        dto.setNombre(request.get("nombre"));
        return actualizarParcial(id, usuarioId, dto);
    }

    @PatchMapping("/{id}/descripcion")
    public ResponseEntity<?> actualizarDescripcion(@PathVariable Long id,
                                                   @RequestParam(required = false) Long usuarioId,
                                                   @RequestBody Map<String, String> request) {
        ActualizarFuenteIngresoDTO dto = new ActualizarFuenteIngresoDTO();
        dto.setDescripcion(request.getOrDefault("descripcion", ""));
        return actualizarParcial(id, usuarioId, dto);
    }

    @GetMapping("/{id}/referencias")
    public ResponseEntity<?> obtenerInfoEliminacion(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", "La fuente con ID " + id + " no existe"));
        }

        ResponseEntity<?> permiso = validarGestionFuente(fuenteExistente.get(), usuarioId, "eliminar");
        if (permiso != null) return permiso;

        FuenteIngreso fuente = fuenteExistente.get();
        long ingresosVinculados = ingresoRepository.countByFuenteIngreso_Id(id);
        return ResponseEntity.ok(new InfoEliminacionFuenteDTO(id, fuente.getNombre(), ingresosVinculados));
    }

    @PostMapping("/{id}/eliminar")
    public ResponseEntity<?> eliminarConOpciones(@PathVariable Long id,
                                                 @RequestParam(required = false) Long usuarioId,
                                                 @RequestBody Map<String, Object> dto) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", "La fuente con ID " + id + " no existe"));
        }

        FuenteIngreso fuente = fuenteExistente.get();
        ResponseEntity<?> permiso = validarGestionFuente(fuente, usuarioId, "eliminar");
        if (permiso != null) return permiso;

        long ingresosVinculados = ingresoRepository.countByFuenteIngreso_Id(id);
        if (ingresosVinculados == 0) {
            fuenteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        boolean confirmar = dto != null && (boolean) dto.getOrDefault("confirmar", false);
        if (!confirmar) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new InfoEliminacionFuenteDTO(id, fuente.getNombre(), ingresosVinculados));
        }

        boolean eliminarTrans = (boolean) dto.getOrDefault("eliminarTransacciones", false);
        if (eliminarTrans) {
            fuenteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new InfoEliminacionFuenteDTO(id, fuente.getNombre(), ingresosVinculados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", "La fuente con ID " + id + " no existe"));
        }

        FuenteIngreso fuente = fuenteExistente.get();
        ResponseEntity<?> permiso = validarGestionFuente(fuente, usuarioId, "eliminar");
        if (permiso != null) return permiso;

        long ingresosVinculados = ingresoRepository.countByFuenteIngreso_Id(id);
        if (ingresosVinculados > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Fuente en uso",
                            "No se puede eliminar. La fuente '" + fuente.getNombre() + "' tiene " + ingresosVinculados + " ingresos vinculados."));
        }

        fuenteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Nombre invalido", "El nombre es obligatorio"));
        }
        if (nombre.trim().length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Nombre invalido", "El nombre no puede superar 50 caracteres"));
        }
        if (!NOMBRE_FUENTE_PERMITIDO.matcher(nombre.trim()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Nombre invalido",
                            "El nombre solo puede contener letras, numeros, espacios, -, _, (), /"));
        }
        return null;
    }

    private boolean existeNombreVisible(String nombre, Long usuarioId, Long ignorarId) {
        return fuenteRepository.findVisiblesPorUsuario(usuarioId).stream()
                .anyMatch(f -> f.getNombre() != null
                        && f.getNombre().equalsIgnoreCase(nombre)
                        && (ignorarId == null || !f.getId().equals(ignorarId)));
    }

    private ResponseEntity<?> validarGestionFuente(FuenteIngreso fuente, Long usuarioId, String accion) {
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Usuario obligatorio", "Debe enviar usuarioId"));
        }
        if (fuente.getUsuario() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO(403, "Fuente predefinida", "Las fuentes predefinidas no se pueden " + accion));
        }
        if (!fuente.getUsuario().getId().equals(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO(403, "Fuente no permitida", "La fuente no pertenece al usuario"));
        }
        return null;
    }

    private boolean esVisibleParaUsuario(FuenteIngreso fuente, Long usuarioId) {
        return fuente.getUsuario() == null
                || (usuarioId != null && fuente.getUsuario().getId().equals(usuarioId));
    }
}
