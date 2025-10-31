package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.service.IAService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AsistenteIAController {

    @Autowired
    private IAService iaService;

    
    @GetMapping("/asistente")
    public String mostrarAsistente(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("mensaje", "Bienvenido al Asistente Nutricional 🤖");
        model.addAttribute("respuesta", ""); 
        return "asistente"; 
    }

    
    @PostMapping("/consultar-ia")
    public String consultarIA(
            @RequestParam("pregunta") String pregunta,
            HttpSession session,
            Model model
    ) {
        try {
            if (session.getAttribute("usuario") == null) {
                return "redirect:/login";
            }

            if (pregunta == null || pregunta.isBlank()) {
                model.addAttribute("mensaje", "⚠️ Por favor, escribe una consulta antes de enviar.");
                return "asistente";
            }

            
            String respuesta = iaService.consultarIA(pregunta);

            model.addAttribute("mensaje", "💬 Respuesta de NutriAI:");
            model.addAttribute("pregunta", pregunta);
            model.addAttribute("respuesta", respuesta);

            return "asistente"; 

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "❌ Error al consultar la IA: " + e.getMessage());
            return "asistente";
        }
    }

    
    @PostMapping("/analizar-foto")
    public String analizarFoto(
            @RequestParam("fotoComida") MultipartFile fotoComida,
            @RequestParam("momento") String momento,
            HttpSession session,
            Model model
    ) {
        try {
            if (session.getAttribute("usuario") == null) {
                return "redirect:/login";
            }

            if (fotoComida == null || fotoComida.isEmpty() || momento == null || momento.isBlank()) {
                model.addAttribute("error", "⚠️ Debes subir una imagen y seleccionar un momento del día.");
                return "asistente";
            }

            
            String resultado = iaService.analizarFoto(fotoComida, momento);

            model.addAttribute("mensaje", "✅ Análisis completado para el " + momento);
            model.addAttribute("respuesta", resultado);

            return "asistente";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "❌ Error al analizar la foto: " + e.getMessage());
            return "asistente";
        }
    }
}
