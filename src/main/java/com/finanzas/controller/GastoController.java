package com.finanzas.controller;

import com.finanzas.dto.GastoCreacionDTO;
import com.finanzas.entity.Categoria;
import com.finanzas.entity.Gasto;
import com.finanzas.entity.TipoCategoria;
import com.finanzas.repository.CategoriaRepository;
import com.finanzas.repository.GastoRepository;
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
    public ResponseEntity<?> crear(@Valid @RequestBody GastoCreacionDTO dto) {
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
        if (categoriaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Categoria categoria = categoriaOpt.get();
        if (categoria.getTipo() != TipoCategoria.GASTO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La categoria debe ser de tipo GASTO");
        }

        Gasto gasto = new Gasto();
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setMonto(BigDecimal.valueOf(dto.getMonto()));
        gasto.setFecha(dto.getFecha());
        gasto.setCategoria(categoria);

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
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Gasto gastoActualizado) {
        Optional<Gasto> gastoOpt = gastoRepository.findById(id);
        if (gastoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (gastoActualizado.getCategoria() == null || gastoActualizado.getCategoria().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La categoria es obligatoria");
        }

        Optional<Categoria> categoriaOpt = categoriaRepository.findById(gastoActualizado.getCategoria().getId());
        if (categoriaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Categoria categoria = categoriaOpt.get();
        if (categoria.getTipo() != TipoCategoria.GASTO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La categoria debe ser de tipo GASTO");
        }

        Gasto gasto = gastoOpt.get();
        gasto.setDescripcion(gastoActualizado.getDescripcion());
        gasto.setMonto(gastoActualizado.getMonto());
        gasto.setFecha(gastoActualizado.getFecha());
        gasto.setCategoria(categoria);

        return ResponseEntity.ok(gastoRepository.save(gasto));
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
