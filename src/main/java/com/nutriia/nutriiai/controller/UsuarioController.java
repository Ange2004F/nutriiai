package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    
    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
                return ResponseEntity.badRequest().body("⚠️ El correo es obligatorio.");
            }

            if (usuarioService.buscarPorCorreo(usuario.getCorreo()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("❌ El correo ya está registrado. Por favor, usa otro.");
            }

            usuarioService.registrarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("✅ Usuario registrado con éxito.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al registrar usuario: " + e.getMessage());
        }
    }

    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuario usuario) {
        try {
            boolean ok = usuarioService.login(usuario.getCorreo(), usuario.getPassword());
            if (ok) {
                return ResponseEntity.ok("✅ Bienvenido " + usuario.getNombres());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("❌ Correo o contraseña incorrectos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error en el inicio de sesión: " + e.getMessage());
        }
    }

    
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.listarUsuarios();
            if (usuarios.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorCorreo(@RequestParam String correo) {
        try {
            Usuario usuario = usuarioService.buscarPorCorreo(correo);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("❌ No se encontró un usuario con el correo: " + correo);
            }
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al buscar usuario: " + e.getMessage());
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable int id) {
        try {
            boolean eliminado = usuarioService.eliminarUsuario(id);
            if (eliminado) {
                return ResponseEntity.ok("🗑️ Usuario eliminado correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("❌ No se encontró el usuario con ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al eliminar usuario: " + e.getMessage());
        }
    }
}
