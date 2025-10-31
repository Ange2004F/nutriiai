package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Historial;
import com.nutriia.nutriiai.repository.HistorialRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HistorialController {

    @Autowired
    private HistorialRepository historialRepository;

    @GetMapping("/historial")
    public String mostrarHistorial(HttpSession session, Model model) {
        String correo = (String) session.getAttribute("usuario");

        if (correo == null) {
            return "redirect:/login";
        }

        try {
            
            List<Historial> historial = historialRepository.findByCorreoOrderByFechaDesc(correo);

            
            if (historial.isEmpty()) {
                model.addAttribute("mensaje", "⚠️ No hay registros en tu historial aún.");
            }

            model.addAttribute("historial", historial);
            return "historial";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "❌ Error al obtener el historial: " + e.getMessage());
            return "error";
        }
    }
}
