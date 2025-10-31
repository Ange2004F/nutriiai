package com.nutriia.nutriiai.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String mostrarError(Model model, HttpSession session) {
        // Obtiene el rol del usuario almacenado en sesión (Administrador, Nutriologo o Paciente)
        String rol = (String) session.getAttribute("rol");

        // Pasa el rol al modelo para que Thymeleaf lo use en el HTML
        model.addAttribute("rol", rol);

        // Puedes definir un mensaje genérico o personalizado
        model.addAttribute("errorMessage", "Ocurrió un problema inesperado en el sistema.");

        // Retorna la vista error.html
        return "error";
    }
}
