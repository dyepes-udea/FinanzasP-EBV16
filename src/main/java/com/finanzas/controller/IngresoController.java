package com.finanzas.controller;

import com.finanzas.dto.IngresoCreacionDTO;
import com.finanzas.entity.FuenteIngreso;
import com.finanzas.entity.Ingreso;
import com.finanzas.repository.CategoriaRepository;
import com.finanzas.repository.FuenteIngresoRepository;
import com.finanzas.repository.IngresoRepository;
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

    public IngresoController(IngresoRepository ingresoRepository, CategoriaRepository categoriaRepository,
                           FuenteIngresoRepository fuenteRepository) {
        this.ingresoRepository = ingresoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fuenteRepository = fuenteRepository;
    }

    @GetMapping
    public List<Ingreso> listar(@RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ingresoRepository.findByCategoria_IdOrderByFechaDesc(categoriaId);
        }
        return ingresoRepository.findAllByOrderByFechaDesc();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody IngresoCreacionDTO dto) {
        if (dto.getFuenteIngresoId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fuente de ingreso es obligatoria");
        }

        Optional<FuenteIngreso> fuenteOpt = fuenteRepository.findById(dto.getFuenteIngresoId());
        if (fuenteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Ingreso ingreso = new Ingreso();
        ingreso.setDescripcion(dto.getDescripcion());
        ingreso.setMonto(BigDecimal.valueOf(dto.getMonto()));
        ingreso.setFecha(dto.getFecha());
        ingreso.setCategoria(null);
        ingreso.setFuenteIngreso(fuenteOpt.get());

        Ingreso guardado = ingresoRepository.save(ingreso);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingreso> obtener(@PathVariable Long id) {
        return ingresoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Ingreso ingresoActualizado) {
        Optional<Ingreso> ingresoOpt = ingresoRepository.findById(id);
        if (ingresoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (ingresoActualizado.getFuenteIngreso() == null || ingresoActualizado.getFuenteIngreso().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fuente de ingreso es obligatoria");
        }

        Optional<FuenteIngreso> fuenteOpt = fuenteRepository.findById(ingresoActualizado.getFuenteIngreso().getId());
        if (fuenteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Ingreso ingreso = ingresoOpt.get();
        ingreso.setDescripcion(ingresoActualizado.getDescripcion());
        ingreso.setMonto(ingresoActualizado.getMonto());
        ingreso.setFecha(ingresoActualizado.getFecha());
        ingreso.setCategoria(null);
        ingreso.setFuenteIngreso(fuenteOpt.get());

        return ResponseEntity.ok(ingresoRepository.save(ingreso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!ingresoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ingresoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
