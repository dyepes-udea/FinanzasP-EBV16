package com.finanzas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finanzas.entity.Gasto;
import com.finanzas.repository.GastoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/gastos")
@CrossOrigin(origins = "*")
public class GastoController {

    private final GastoRepository gastoRepository;

    public GastoController(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    @GetMapping
    public List<Gasto> listar() {
        return gastoRepository.findAllByOrderByFechaDesc();
    }

    @PostMapping
    public ResponseEntity<Gasto> crear(@Valid @RequestBody Gasto gasto) {
        Gasto guardado = gastoRepository.save(gasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
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
