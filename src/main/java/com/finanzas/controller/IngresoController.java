package com.finanzas.controller;

import com.finanzas.dto.IngresoCreacionDTO;
import com.finanzas.entity.FuenteIngreso;
import com.finanzas.entity.Ingreso;
import com.finanzas.entity.Usuario;
import com.finanzas.repository.CategoriaRepository;
import com.finanzas.repository.FuenteIngresoRepository;
import com.finanzas.repository.IngresoRepository;
import com.finanzas.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingresos")
@CrossOrigin(origins = "*")
public class IngresoController {

    private final IngresoRepository ingresoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FuenteIngresoRepository fuenteRepository;
    private final UsuarioRepository usuarioRepository;

    public IngresoController(IngresoRepository ingresoRepository, CategoriaRepository categoriaRepository,
                           FuenteIngresoRepository fuenteRepository, UsuarioRepository usuarioRepository) {
        this.ingresoRepository = ingresoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fuenteRepository = fuenteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam Long usuarioId, @RequestParam(required = false) Long categoriaId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no existe");
        }

        if (categoriaId != null) {
            return ResponseEntity.ok(ingresoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, categoriaId));
        }
        return ResponseEntity.ok(ingresoRepository.findByUsuario_IdOrderByFechaDesc(usuarioId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody IngresoCreacionDTO dto) {
        if (dto.getFuenteIngresoId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fuente de ingreso es obligatoria");
        }

        Optional<FuenteIngreso> fuenteOpt = fuenteRepository.findById(dto.getFuenteIngresoId());
        if (fuenteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no existe");
        }

        if (!fuenteVisibleParaUsuario(fuenteOpt.get(), dto.getUsuarioId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La fuente de ingreso no pertenece al usuario");
        }

        Ingreso ingreso = new Ingreso();
        ingreso.setDescripcion(dto.getDescripcion());
        ingreso.setMonto(BigDecimal.valueOf(dto.getMonto()));
        ingreso.setFecha(dto.getFecha());
        ingreso.setCategoria(null);
        ingreso.setFuenteIngreso(fuenteOpt.get());
        ingreso.setUsuario(usuarioOpt.get());

        Ingreso guardado = ingresoRepository.save(ingreso);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuarioId es obligatorio");
        }

        Optional<Ingreso> ingresoOpt = ingresoRepository.findById(id);
        if (ingresoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!perteneceAlUsuario(ingresoOpt.get(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El ingreso no pertenece al usuario");
        }

        return ResponseEntity.ok(ingresoOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestParam(required = false) Long usuarioId,
                                        @RequestBody Ingreso ingresoActualizado) {
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuarioId es obligatorio");
        }

        Optional<Ingreso> ingresoOpt = ingresoRepository.findById(id);
        if (ingresoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!perteneceAlUsuario(ingresoOpt.get(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El ingreso no pertenece al usuario");
        }

        if (ingresoActualizado.getDescripcion() == null || ingresoActualizado.getDescripcion().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La descripcion es obligatoria");
        }
        if (ingresoActualizado.getMonto() == null || ingresoActualizado.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El monto debe ser positivo");
        }
        if (ingresoActualizado.getFecha() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fecha es obligatoria");
        }
        if (ingresoActualizado.getFuenteIngreso() == null || ingresoActualizado.getFuenteIngreso().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fuente de ingreso es obligatoria");
        }

        Optional<FuenteIngreso> fuenteOpt = fuenteRepository.findById(ingresoActualizado.getFuenteIngreso().getId());
        if (fuenteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!fuenteVisibleParaUsuario(fuenteOpt.get(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La fuente de ingreso no pertenece al usuario");
        }

        Ingreso ingreso = ingresoOpt.get();
        ingreso.setDescripcion(ingresoActualizado.getDescripcion().trim());
        ingreso.setMonto(ingresoActualizado.getMonto());
        ingreso.setFecha(ingresoActualizado.getFecha());
        ingreso.setCategoria(null);
        ingreso.setFuenteIngreso(fuenteOpt.get());

        return ResponseEntity.ok(ingresoRepository.save(ingreso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuarioId es obligatorio");
        }

        Optional<Ingreso> ingresoOpt = ingresoRepository.findById(id);
        if (ingresoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!perteneceAlUsuario(ingresoOpt.get(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El ingreso no pertenece al usuario");
        }

        ingresoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean perteneceAlUsuario(Ingreso ingreso, Long usuarioId) {
        return ingreso.getUsuario() != null
                && ingreso.getUsuario().getId() != null
                && ingreso.getUsuario().getId().equals(usuarioId);
    }

    private boolean fuenteVisibleParaUsuario(FuenteIngreso fuente, Long usuarioId) {
        return fuente.getUsuario() != null
                && fuente.getUsuario().getId() != null
                && fuente.getUsuario().getId().equals(usuarioId);
    }
}
