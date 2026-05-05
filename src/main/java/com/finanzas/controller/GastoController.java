package com.finanzas.controller;

import com.finanzas.entity.Gasto;
import com.finanzas.entity.Categoria;
import com.finanzas.dto.GastoCreacionDTO;
import com.finanzas.repository.GastoRepository;
import com.finanzas.repository.CategoriaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gastos")
@CrossOrigin(origins = "*")
public class GastoController {

    private final GastoRepository gastoRepository;
    private final CategoriaRepository categoriaRepository;

    public GastoController(GastoRepository gastoRepository, CategoriaRepository categoriaRepository) {
        this.gastoRepository = gastoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public List<Gasto> listar(@RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return gastoRepository.findByCategoria_IdOrderByFechaDesc(categoriaId);
        }
        return gastoRepository.findAllByOrderByFechaDesc();
    }

    @PostMapping
    public ResponseEntity<Gasto> crear(@Valid @RequestBody GastoCreacionDTO dto) {
        // Validar que la categoría existe
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
        if (categoriaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Crear el gasto con la categoría
        Gasto gasto = new Gasto();
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setMonto(BigDecimal.valueOf(dto.getMonto()));
        gasto.setFecha(dto.getFecha());
        gasto.setCategoria(categoriaOpt.get());

        Gasto guardado = gastoRepository.save(gasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gasto> obtener(@PathVariable Long id) {
        return gastoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gasto> actualizar(@PathVariable Long id, @Valid @RequestBody Gasto gastoActualizado) {
        return gastoRepository.findById(id)
                .map(gasto -> {
                    // Validar que la categoría existe
                    if (gastoActualizado.getCategoria() == null || gastoActualizado.getCategoria().getId() == null) {
                        throw new IllegalArgumentException("La categoría es obligatoria");
                    }
                    
                    if (!categoriaRepository.existsById(gastoActualizado.getCategoria().getId())) {
                        throw new IllegalArgumentException("La categoría especificada no existe");
                    }

                    gasto.setDescripcion(gastoActualizado.getDescripcion());
                    gasto.setMonto(gastoActualizado.getMonto());
                    gasto.setFecha(gastoActualizado.getFecha());
                    gasto.setCategoria(gastoActualizado.getCategoria());

                    return ResponseEntity.ok(gastoRepository.save(gasto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!gastoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        gastoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
