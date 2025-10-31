package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/admin")
    public String panelAdmin(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        Object rol = session.getAttribute("rol");

        if (usuario == null || rol == null || !rol.toString().equalsIgnoreCase("Administrador")) {
            return "redirect:/login";
        }

        model.addAttribute("titulo", "Panel de Administración");
        model.addAttribute("mensaje", "Bienvenido al panel de control, " + session.getAttribute("nombre") + " 👑");
        model.addAttribute("correo", session.getAttribute("usuario"));
        model.addAttribute("rol", rol.toString());

        return "admin";
    }

    @GetMapping("/admin/usuarios")
    public String gestionarUsuarios(HttpSession session, Model model) {
        Object rol = session.getAttribute("rol");
        if (rol == null || !rol.toString().equalsIgnoreCase("Administrador")) {
            return "redirect:/login";
        }

        try {
            List<Usuario> usuarios = usuarioRepository.findAll();

            model.addAttribute("usuarios", usuarios);
            model.addAttribute("titulo", "Gestión de Usuarios");
            model.addAttribute("mensaje", "Aquí podrás visualizar y administrar los usuarios del sistema 🧠");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "⚠️ Error al obtener la lista de usuarios: " + e.getMessage());
        }

        return "admin_usuarios";
    }

@GetMapping("/admin/reportes")
public String verReportes(HttpSession session, Model model) {
    Object rol = session.getAttribute("rol");
    if (rol == null || !rol.toString().equalsIgnoreCase("Administrador")) {
        return "redirect:/login";
    }

    try {
        long totalUsuarios = usuarioRepository.count();

       
        long totalRoles = 3; 
        long totalPlanes = 0; 
        long totalProgresos = 0; 

        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalRoles", totalRoles);
        model.addAttribute("totalPlanes", totalPlanes);
        model.addAttribute("totalProgresos", totalProgresos);

        model.addAttribute("titulo", "Reportes del Sistema");
        model.addAttribute("mensaje", "Datos generales del funcionamiento de NutriAI 🧾");

    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "⚠️ Error al generar los reportes: " + e.getMessage());
    }

    return "admin_reportes";
 }
}
