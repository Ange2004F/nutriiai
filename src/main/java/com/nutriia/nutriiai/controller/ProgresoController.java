package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Progreso;
import com.nutriia.nutriiai.repository.ProgresoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProgresoController {

    @Autowired
    private ProgresoRepository progresoRepository;

    @GetMapping("/progreso")
    public String mostrarProgreso(HttpSession session, Model model) {
        String correo = (String) session.getAttribute("usuario");

        
        if (correo == null) {
            return "redirect:/login";
        }

        try {
            
            List<Progreso> historial = progresoRepository.findByUsuarioCorreoOrderByFechaDesc(correo);

            if (historial.isEmpty()) {
                model.addAttribute("mensaje", "⚠ No hay registros de progreso aún.");
            } else {
                model.addAttribute("historial", historial);
                model.addAttribute("mensaje", "Seguimiento de tu avance nutricional 📈");
            }

            model.addAttribute("titulo", "Historial de Progreso");

            return "progreso";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "⚠ Error al cargar el historial de progreso: " + e.getMessage());
            return "error";
        }
    }
}
