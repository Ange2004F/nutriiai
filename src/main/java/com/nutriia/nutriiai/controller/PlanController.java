package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Plan;
import com.nutriia.nutriiai.repository.PlanRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.Element;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class PlanController {

    @Autowired
    private PlanRepository planRepository;

    @GetMapping("/plan")
    public String verPlan(HttpSession session, Model model) {
        Integer usuarioId = (Integer) session.getAttribute("idUsuario");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            
            List<Plan> planes = planRepository.findByUsuarioIdOrderByDia(usuarioId);

            if (planes.isEmpty()) {
                model.addAttribute("mensaje", "⚠ No tienes un plan de alimentación registrado aún.");
            }

            
            Map<String, Plan> unicoPorDia = new LinkedHashMap<>();
            List<String> ordenDias = Arrays.asList(
                    "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
            );

            for (Plan plan : planes) {
                if (!unicoPorDia.containsKey(plan.getDia())) {
                    unicoPorDia.put(plan.getDia(), plan);
                }
            }

           
            List<Plan> planesOrdenados = new ArrayList<>();
            for (String dia : ordenDias) {
                if (unicoPorDia.containsKey(dia)) {
                    planesOrdenados.add(unicoPorDia.get(dia));
                }
            }

           
            model.addAttribute("planes", planesOrdenados);
            model.addAttribute("titulo", "Plan Semanal - NutriAI");

            return "plan";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "⚠ Error al cargar el plan: " + e.getMessage());
            return "error";
        }
    }

    
    @Transactional
    @PostMapping("/plan/borrar")
    public String borrarPlan(HttpSession session, Model model) {
        try {
            Integer usuarioId = (Integer) session.getAttribute("idUsuario");
            if (usuarioId == null) {
                return "redirect:/login";
            }

            
            planRepository.deleteByUsuarioId(usuarioId);

            
            model.addAttribute("mensaje", "🗑 Tu plan semanal ha sido eliminado correctamente.");
            model.addAttribute("planes", null);

            return "redirect:/plan";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "⚠ No se pudo eliminar el plan: " + e.getMessage());
            return "error";
        }
    }

    
  @GetMapping("/exportarPDF")
public void exportarPDF(HttpSession session, HttpServletResponse response) throws IOException {
    Integer usuarioId = (Integer) session.getAttribute("idUsuario");
    if (usuarioId == null) {
        response.sendRedirect("/login");
        return;
    }

    List<Plan> planes = planRepository.findAll()
        .stream()
        .filter(p -> p.getUsuarioId() == usuarioId)
        .sorted(Comparator.comparing(Plan::getDia))
        .toList();

    if (planes.isEmpty()) {
        response.sendRedirect("/plan");
        return;
    }

   
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=plan_semanal.pdf");

    try {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.CYAN);
        Paragraph titulo = new Paragraph("Plan Semanal de Alimentación - NutriAI\n\n", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Font diaFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.GREEN);
        Font textoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

        String diaActual = "";
        for (Plan plan : planes) {
            if (!plan.getDia().equals(diaActual)) {
                document.add(new Paragraph("\n" + plan.getDia(), diaFont));
                diaActual = plan.getDia();
            }
            document.add(new Paragraph("• " + plan.getComida() + ": " + plan.getPlato(), textoFont));
        }

        document.close();

    } catch (DocumentException e) {
        throw new IOException(e.getMessage());
    }
}

}
