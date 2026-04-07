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

import com.finanzas.entity.Ingreso;
import com.finanzas.repository.IngresoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ingresos")
@CrossOrigin(origins = "*")
public class IngresoController {

    private final IngresoRepository ingresoRepository;

    public IngresoController(IngresoRepository ingresoRepository) {
        this.ingresoRepository = ingresoRepository;
    }

    @GetMapping
    public List<Ingreso> listar() {
        return ingresoRepository.findAllByOrderByFechaDesc();
    }

    @PostMapping
    public ResponseEntity<Ingreso> crear(@Valid @RequestBody Ingreso ingreso) {
        Ingreso guardado = ingresoRepository.save(ingreso);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
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
