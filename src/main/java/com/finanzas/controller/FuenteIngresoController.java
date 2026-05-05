package com.finanzas.controller;

import com.finanzas.entity.FuenteIngreso;
import com.finanzas.repository.FuenteIngresoRepository;
import com.finanzas.repository.IngresoRepository;
import com.finanzas.dto.ActualizarFuenteIngresoDTO;
import com.finanzas.dto.ErrorResponseDTO;
import com.finanzas.dto.InfoEliminacionFuenteDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/fuentes-ingreso")
@CrossOrigin(origins = "*")
public class FuenteIngresoController {

    private final FuenteIngresoRepository fuenteRepository;
    private final IngresoRepository ingresoRepository;

    public FuenteIngresoController(FuenteIngresoRepository fuenteRepository, IngresoRepository ingresoRepository) {
        this.fuenteRepository = fuenteRepository;
        this.ingresoRepository = ingresoRepository;
    }

    @GetMapping
    public List<FuenteIngreso> listar() {
        return fuenteRepository.findAllByOrderByNombre();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<FuenteIngreso> fuente = fuenteRepository.findById(id);
        if (fuente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", 
                            "La fuente de ingreso con ID " + id + " no existe"));
        }
        return ResponseEntity.ok(fuente.get());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody FuenteIngreso fuente) {
        if (fuenteRepository.findByNombre(fuente.getNombre()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Nombre duplicado", 
                            "Ya existe una fuente con el nombre '" + fuente.getNombre() + "'"));
        }
        FuenteIngreso guardada = fuenteRepository.save(fuente);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, 
                                        @Valid @RequestBody ActualizarFuenteIngresoDTO actualizar) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", 
                            "La fuente con ID " + id + " no existe"));
        }

        Optional<FuenteIngreso> otra = fuenteRepository.findByNombre(actualizar.getNombre());
        if (otra.isPresent() && !otra.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Nombre duplicado", 
                            "Ya existe otra fuente con el nombre '" + actualizar.getNombre() + "'"));
        }

        FuenteIngreso fuente = fuenteExistente.get();
        fuente.setNombre(actualizar.getNombre());
        fuente.setDescripcion(actualizar.getDescripcion());

        FuenteIngreso actualizada = fuenteRepository.save(fuente);
        return ResponseEntity.ok(actualizada);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarParcial(@PathVariable Long id, 
                                               @RequestBody ActualizarFuenteIngresoDTO actualizar) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", 
                            "La fuente con ID " + id + " no existe"));
        }

        FuenteIngreso fuente = fuenteExistente.get();

        if (actualizar.getNombre() != null && !actualizar.getNombre().isEmpty()) {
            Optional<FuenteIngreso> otra = fuenteRepository.findByNombre(actualizar.getNombre());
            if (otra.isPresent() && !otra.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponseDTO(409, "Nombre duplicado", 
                                "Ya existe otra fuente con el nombre '" + actualizar.getNombre() + "'"));
            }
            fuente.setNombre(actualizar.getNombre());
        }

        if (actualizar.getDescripcion() != null) {
            fuente.setDescripcion(actualizar.getDescripcion());
        }

        FuenteIngreso actualizada = fuenteRepository.save(fuente);
        return ResponseEntity.ok(actualizada);
    }

    @PatchMapping("/{id}/nombre")
    public ResponseEntity<?> actualizarNombre(@PathVariable Long id, 
                                              @RequestBody Map<String, String> request) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", 
                            "La fuente con ID " + id + " no existe"));
        }

        String nuevoNombre = request.get("nombre");
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Nombre inválido", 
                            "El nombre no puede estar vacío"));
        }

        Optional<FuenteIngreso> otra = fuenteRepository.findByNombre(nuevoNombre);
        if (otra.isPresent() && !otra.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Nombre duplicado", 
                            "Ya existe otra fuente con el nombre '" + nuevoNombre + "'"));
        }

        FuenteIngreso fuente = fuenteExistente.get();
        fuente.setNombre(nuevoNombre);
        FuenteIngreso actualizada = fuenteRepository.save(fuente);
        return ResponseEntity.ok(actualizada);
    }

    @PatchMapping("/{id}/descripcion")
    public ResponseEntity<?> actualizarDescripcion(@PathVariable Long id, 
                                                   @RequestBody Map<String, String> request) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", 
                            "La fuente con ID " + id + " no existe"));
        }

        String nuevaDescripcion = request.get("descripcion");
        FuenteIngreso fuente = fuenteExistente.get();
        fuente.setDescripcion(nuevaDescripcion != null ? nuevaDescripcion : "");
        
        FuenteIngreso actualizada = fuenteRepository.save(fuente);
        return ResponseEntity.ok(actualizada);
    }

    @GetMapping("/{id}/referencias")
    public ResponseEntity<?> obtenerInfoEliminacion(@PathVariable Long id) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", 
                            "La fuente con ID " + id + " no existe"));
        }

        FuenteIngreso fuente = fuenteExistente.get();
        long ingresosVinculados = ingresoRepository.countByCategoria_Id(id);

        InfoEliminacionFuenteDTO info = new InfoEliminacionFuenteDTO(id, fuente.getNombre(), ingresosVinculados);
        return ResponseEntity.ok(info);
    }

    @PostMapping("/{id}/eliminar")
    public ResponseEntity<?> eliminarConOpciones(@PathVariable Long id, @RequestBody Map<String, Object> dto) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);

        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", 
                            "La fuente con ID " + id + " no existe"));
        }

        FuenteIngreso fuente = fuenteExistente.get();
        long ingresosVinculados = ingresoRepository.countByCategoria_Id(id);

        if (ingresosVinculados == 0) {
            fuenteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        boolean confirmar = dto != null && (boolean) dto.getOrDefault("confirmar", false);
        if (!confirmar) {
            InfoEliminacionFuenteDTO info = new InfoEliminacionFuenteDTO(id, fuente.getNombre(), ingresosVinculados);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(info);
        }

        boolean eliminarTrans = (boolean) dto.getOrDefault("eliminarTransacciones", false);

        if (eliminarTrans) {
            fuenteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        InfoEliminacionFuenteDTO info = new InfoEliminacionFuenteDTO(id, fuente.getNombre(), ingresosVinculados);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(info);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<FuenteIngreso> fuenteExistente = fuenteRepository.findById(id);
        
        if (fuenteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Fuente no encontrada", 
                            "La fuente con ID " + id + " no existe"));
        }

        FuenteIngreso fuente = fuenteExistente.get();
        long ingresosVinculados = ingresoRepository.countByCategoria_Id(id);

        if (ingresosVinculados > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Fuente en uso",
                            "No se puede eliminar. La fuente '" + fuente.getNombre() + 
                            "' tiene " + ingresosVinculados + 
                            " ingresos vinculados. Reasigne o elimine los ingresos primero."));
        }

        fuenteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
