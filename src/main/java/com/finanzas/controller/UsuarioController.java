package com.finanzas.controller;

import com.finanzas.dto.ActualizarNombreUsuarioDTO;
import com.finanzas.dto.ActualizarFotoUsuarioDTO;
import com.finanzas.dto.ErrorResponseDTO;
import com.finanzas.entity.Usuario;
import com.finanzas.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private static final Path DIRECTORIO_FOTOS = Path.of("src", "main", "resources", "static", "uploads", "perfiles");

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PatchMapping("/{id}/nombre")
    public ResponseEntity<?> actualizarNombre(@PathVariable Long id, @RequestBody ActualizarNombreUsuarioDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Usuario no encontrado", "El usuario con ID " + id + " no existe"));
        }

        String nombre = dto != null && dto.getNombre() != null ? dto.getNombre().trim() : "";
        if (nombre.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Nombre invalido", "El nombre no puede estar vacio"));
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setNombre(nombre);
        Usuario actualizado = usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of(
                "id", actualizado.getId(),
                "correo", actualizado.getCorreo(),
                "nombre", actualizado.getNombre() != null ? actualizado.getNombre() : "",
                "fotoUrl", actualizado.getFotoUrl() != null ? actualizado.getFotoUrl() : ""
        ));
    }

    @PatchMapping("/{id}/foto")
    public ResponseEntity<?> actualizarFoto(@PathVariable Long id, @RequestBody ActualizarFotoUsuarioDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Usuario no encontrado", "El usuario con ID " + id + " no existe"));
        }

        String fotoUrl = dto != null && dto.getFotoUrl() != null ? dto.getFotoUrl().trim() : "";
        if (fotoUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Foto invalida", "La URL de la foto no puede estar vacia"));
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setFotoUrl(fotoUrl);
        Usuario actualizado = usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of(
                "id", actualizado.getId(),
                "correo", actualizado.getCorreo(),
                "nombre", actualizado.getNombre() != null ? actualizado.getNombre() : "",
                "fotoUrl", actualizado.getFotoUrl() != null ? actualizado.getFotoUrl() : ""
        ));
    }

    @PostMapping("/{id}/foto-upload")
    public ResponseEntity<?> subirFoto(@PathVariable Long id, @RequestParam(value = "foto", required = false) MultipartFile foto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Usuario no encontrado", "El usuario con ID " + id + " no existe"));
        }

        if (foto == null || foto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Foto obligatoria", "Debe seleccionar una imagen"));
        }

        String extension = obtenerExtensionPermitida(foto.getContentType());
        if (extension == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Archivo invalido", "Solo se permiten imagenes JPG, PNG, WEBP o GIF"));
        }

        try {
            Files.createDirectories(DIRECTORIO_FOTOS);
            String nombreArchivo = "usuario-" + id + "-" + System.currentTimeMillis() + extension;
            Path destino = DIRECTORIO_FOTOS.resolve(nombreArchivo);
            Files.copy(foto.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            Usuario usuario = usuarioOpt.get();
            usuario.setFotoUrl("/uploads/perfiles/" + nombreArchivo);
            Usuario actualizado = usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of(
                    "id", actualizado.getId(),
                    "correo", actualizado.getCorreo(),
                    "nombre", actualizado.getNombre() != null ? actualizado.getNombre() : "",
                    "fotoUrl", actualizado.getFotoUrl() != null ? actualizado.getFotoUrl() : ""
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO(500, "Error guardando foto", "No se pudo guardar la imagen"));
        }
    }

    private String obtenerExtensionPermitida(String contentType) {
        if (contentType == null) {
            return null;
        }

        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> null;
        };
    }
}
