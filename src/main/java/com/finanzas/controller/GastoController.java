package com.finanzas.controller;

import com.finanzas.dto.GastoCreacionDTO;
import com.finanzas.entity.Categoria;
import com.finanzas.entity.Gasto;
import com.finanzas.entity.TipoCategoria;
import com.finanzas.entity.Usuario;
import com.finanzas.repository.CategoriaRepository;
import com.finanzas.repository.GastoRepository;
import com.finanzas.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    public GastoController(GastoRepository gastoRepository, CategoriaRepository categoriaRepository,
                           UsuarioRepository usuarioRepository) {
        this.gastoRepository = gastoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam Long usuarioId, @RequestParam(required = false) Long categoriaId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no existe");
        }

        if (categoriaId != null) {
            return ResponseEntity.ok(gastoRepository.findByUsuario_IdAndCategoria_IdOrderByFechaDesc(usuarioId, categoriaId));
        }
        return ResponseEntity.ok(gastoRepository.findByUsuario_IdOrderByFechaDesc(usuarioId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody GastoCreacionDTO dto) {
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
        if (categoriaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no existe");
        }

        Categoria categoria = categoriaOpt.get();
        if (categoria.getTipo() != TipoCategoria.GASTO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La categoria debe ser de tipo GASTO");
        }
        if (!categoriaVisibleParaUsuario(categoria, dto.getUsuarioId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La categoria no pertenece al usuario");
        }

        Gasto gasto = new Gasto();
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setMonto(BigDecimal.valueOf(dto.getMonto()));
        gasto.setFecha(dto.getFecha());
        gasto.setCategoria(categoria);
        gasto.setUsuario(usuarioOpt.get());

        Gasto guardado = gastoRepository.save(gasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuarioId es obligatorio");
        }

        Optional<Gasto> gastoOpt = gastoRepository.findById(id);
        if (gastoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!perteneceAlUsuario(gastoOpt.get(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El gasto no pertenece al usuario");
        }

        return ResponseEntity.ok(gastoOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestParam(required = false) Long usuarioId,
                                        @RequestBody Gasto gastoActualizado) {
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuarioId es obligatorio");
        }

        Optional<Gasto> gastoOpt = gastoRepository.findById(id);
        if (gastoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!perteneceAlUsuario(gastoOpt.get(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El gasto no pertenece al usuario");
        }

        if (gastoActualizado.getDescripcion() == null || gastoActualizado.getDescripcion().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La descripcion es obligatoria");
        }
        if (gastoActualizado.getMonto() == null || gastoActualizado.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El monto debe ser positivo");
        }
        if (gastoActualizado.getFecha() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fecha es obligatoria");
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
        if (!categoriaVisibleParaUsuario(categoria, usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La categoria no pertenece al usuario");
        }

        Gasto gasto = gastoOpt.get();
        gasto.setDescripcion(gastoActualizado.getDescripcion().trim());
        gasto.setMonto(gastoActualizado.getMonto());
        gasto.setFecha(gastoActualizado.getFecha());
        gasto.setCategoria(categoria);

        return ResponseEntity.ok(gastoRepository.save(gasto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuarioId es obligatorio");
        }

        Optional<Gasto> gastoOpt = gastoRepository.findById(id);
        if (gastoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!perteneceAlUsuario(gastoOpt.get(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El gasto no pertenece al usuario");
        }

        gastoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean perteneceAlUsuario(Gasto gasto, Long usuarioId) {
        return gasto.getUsuario() != null
                && gasto.getUsuario().getId() != null
                && gasto.getUsuario().getId().equals(usuarioId);
    }

    private boolean categoriaVisibleParaUsuario(Categoria categoria, Long usuarioId) {
        return categoria.getUsuario() != null
                && categoria.getUsuario().getId() != null
                && categoria.getUsuario().getId().equals(usuarioId);
    }
}
