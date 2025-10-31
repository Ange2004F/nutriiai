package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recuperar")
public class RecuperarContrasena {

    @Autowired
    private UsuarioRepository usuarioRepository;

    
    @GetMapping
    public String mostrarFormulario() {
        return "recuperar";
    }

    
    @PostMapping
    public String procesarRecuperacion(@RequestParam("correo") String correo, Model model) {
        try {
           
            Usuario usuario = usuarioRepository.findByCorreo(correo);

            if (usuario != null) {
               
                model.addAttribute("mensaje", "✅ Se envió un enlace de recuperación a " + correo);
                model.addAttribute("usuario", usuario.getNombres());
            } else {
                model.addAttribute("mensaje", "❌ No existe una cuenta con ese correo.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "⚠ Error al intentar recuperar la contraseña: " + e.getMessage());
        }

        return "recuperar";
    }
}
