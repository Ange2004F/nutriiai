package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    private final UsuarioService usuarioService;

    public UsuarioViewController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login"; 
    }

   
    @PostMapping("/login")
    public String doLogin(@ModelAttribute("usuario") Usuario form,
                          HttpSession session,
                          Model model) {
        boolean ok = usuarioService.login(form.getCorreo(), form.getPassword());

        if (ok) {
            
            Usuario user = usuarioService.buscarPorCorreo(form.getCorreo());
            session.setAttribute("usuario", user.getCorreo());
            session.setAttribute("nombre", user.getNombres());
            session.setAttribute("rol", user.getRol());
            session.setAttribute("idUsuario", user.getId());

            
            if ("Administrador".equalsIgnoreCase(user.getRol())) {
                return "redirect:/admin";
            } else if ("Nutriologo".equalsIgnoreCase(user.getRol())) {
                return "redirect:/nutriologo";
            } else {
                return "redirect:/bienvenido";
            }
        }

        model.addAttribute("error", "❌ Correo o contraseña incorrectos");
        return "login";
    }

   
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; 
    }

    
    @PostMapping("/registro")
    public String doRegistro(@ModelAttribute("usuario") Usuario form, Model model) {
       
        if (usuarioService.buscarPorCorreo(form.getCorreo()) != null) {
            model.addAttribute("error", "⚠️ El correo ya está registrado");
            return "registro";
        }

       
        if (form.getNombres() == null || form.getNombres().isBlank()
                || form.getApellidos() == null || form.getApellidos().isBlank()
                || form.getCorreo() == null || form.getCorreo().isBlank()
                || form.getPassword() == null || form.getPassword().isBlank()) {
            model.addAttribute("error", "⚠️ Todos los campos son obligatorios");
            return "registro";
        }

        try {
            usuarioService.registrarUsuario(form);
            model.addAttribute("mensaje", "✅ Registro exitoso. Inicia sesión para continuar.");
            return "redirect:/usuarios/login";
        } catch (Exception e) {
            model.addAttribute("error", "⚠️ Error al registrar usuario: " + e.getMessage());
            return "registro";
        }
    }

    
    @GetMapping("/lista")
    public String lista(HttpSession session, Model model) {
        Object rol = session.getAttribute("rol");
        if (rol == null || !rol.toString().equalsIgnoreCase("Administrador")) {
            return "redirect:/usuarios/login";
        }

        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("titulo", "Lista de Usuarios");
        return "lista"; 
    }

 
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuarios/login?logout";
    }
}
