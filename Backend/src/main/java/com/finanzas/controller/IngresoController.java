package com.finanzas.controller;

import com.finanzas.entity.Ingreso;
import com.finanzas.entity.Categoria;
import com.finanzas.dto.IngresoCreacionDTO;
import com.finanzas.repository.IngresoRepository;
import com.finanzas.repository.CategoriaRepository;
import com.finanzas.repository.FuenteIngresoRepository;
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
    public ResponseEntity<Ingreso> crear(@Valid @RequestBody IngresoCreacionDTO dto) {
        // Validar que la categoría existe
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
        if (categoriaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Crear el ingreso con la categoría
        Ingreso ingreso = new Ingreso();
        ingreso.setDescripcion(dto.getDescripcion());
        ingreso.setMonto(BigDecimal.valueOf(dto.getMonto()));
        ingreso.setFecha(dto.getFecha());
        ingreso.setCategoria(categoriaOpt.get());

        // Asignar fuente de ingreso si se proporciona
        if (dto.getFuenteIngresoId() != null) {
            var fuenteOpt = fuenteRepository.findById(dto.getFuenteIngresoId());
            fuenteOpt.ifPresent(ingreso::setFuenteIngreso);
        }

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
    public ResponseEntity<Ingreso> actualizar(@PathVariable Long id, @Valid @RequestBody Ingreso ingresoActualizado) {
        return ingresoRepository.findById(id)
                .map(ingreso -> {
                    // Validar que la categoría existe
                    if (ingresoActualizado.getCategoria() == null || ingresoActualizado.getCategoria().getId() == null) {
                        throw new IllegalArgumentException("La categoría es obligatoria");
                    }
                    
                    if (!categoriaRepository.existsById(ingresoActualizado.getCategoria().getId())) {
                        throw new IllegalArgumentException("La categoría especificada no existe");
                    }

                    ingreso.setDescripcion(ingresoActualizado.getDescripcion());
                    ingreso.setMonto(ingresoActualizado.getMonto());
                    ingreso.setFecha(ingresoActualizado.getFecha());
                    ingreso.setCategoria(ingresoActualizado.getCategoria());

                    return ResponseEntity.ok(ingresoRepository.save(ingreso));
                })
                .orElse(ResponseEntity.notFound().build());
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
