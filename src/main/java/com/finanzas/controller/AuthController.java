package com.finanzas.controller;

import com.finanzas.dto.ErrorResponseDTO;
import com.finanzas.dto.LoginUsuarioDTO;
import com.finanzas.dto.RegistroUsuarioDTO;
import com.finanzas.entity.Usuario;
import com.finanzas.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Pattern CORREO_VALIDO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroUsuarioDTO dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Datos invalidos", "Debe enviar correo y contrasena"));
        }

        String correo = dto.getCorreo() != null ? dto.getCorreo().trim().toLowerCase() : "";
        String contrasena = dto.getContrasena();

        if (correo.isEmpty() || !CORREO_VALIDO.matcher(correo).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Correo invalido", "El correo no es valido"));
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Contrasena obligatoria", "La contrasena es obligatoria"));
        }

        if (usuarioRepository.existsByCorreo(correo)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(409, "Correo en uso", "El correo ya esta en uso"));
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuario.setNombre("");
        usuario.setFotoUrl("");

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", guardado.getId(),
                        "correo", guardado.getCorreo(),
                        "mensaje", "Cuenta creada exitosamente"
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUsuarioDTO dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Datos invalidos", "El correo y la contrasena son obligatorios"));
        }

        String correo = dto.getCorreo() != null ? dto.getCorreo().trim().toLowerCase() : "";
        String contrasena = dto.getContrasena();

        if (correo.isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Datos incompletos", "El correo y la contrasena son obligatorios"));
        }

        return usuarioRepository.findByCorreo(correo)
                .filter(usuario -> usuario.getContrasena().equals(contrasena))
                .<ResponseEntity<?>>map(usuario -> ResponseEntity.ok(Map.of(
                        "id", usuario.getId(),
                        "correo", usuario.getCorreo(),
                        "nombre", usuario.getNombre() != null ? usuario.getNombre() : "",
                        "fotoUrl", usuario.getFotoUrl() != null ? usuario.getFotoUrl() : ""
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponseDTO(401, "Credenciales invalidas", "Usuario o contraseña incorrectos")));
    }
}
