package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.service.PlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BorrarPlanController {

    @Autowired
    private PlanService planService;

    @PostMapping("/planes/borrar")
    public String borrarPlan(HttpSession session) {
        
        if (session == null || session.getAttribute("idUsuario") == null) {
            return "redirect:/login";
        }

        int usuarioId = (int) session.getAttribute("idUsuario");

        try {
            planService.borrarPlanPorUsuario(usuarioId);
            session.setAttribute("mensaje", "🗑️ Tu plan fue eliminado correctamente.");
            return "redirect:/planes";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "❌ Error al eliminar tu plan: " + e.getMessage());
            return "error";
        }
    }
}
