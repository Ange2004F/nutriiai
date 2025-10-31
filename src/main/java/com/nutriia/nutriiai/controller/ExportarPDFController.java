package com.nutriia.nutriiai.controller;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;

import com.itextpdf.text.pdf.PdfWriter;
import com.nutriia.nutriiai.model.Plan;
import com.nutriia.nutriiai.repository.PlanRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ExportarPDFController {

    @Autowired
    private PlanRepository planRepository;

    @GetMapping("/exportar-pdf")
    public void exportarPDF(HttpSession session, HttpServletResponse response) {
        try {
            
            if (session == null || session.getAttribute("idUsuario") == null) {
                response.sendRedirect("/login");
                return;
            }

            String nombreUsuario = (String) session.getAttribute("nombre");
            int usuarioId = (int) session.getAttribute("idUsuario");
            int kcal = session.getAttribute("kcal_diarias") != null
                    ? (int) session.getAttribute("kcal_diarias")
                    : 0;

           
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=nutri_resumen.pdf");

            Document document = new Document();
            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            
            try {
                Image logo = Image.getInstance("src/main/resources/static/img/logo_n.png");
                logo.scaleToFit(100, 100);
                logo.setAlignment(Image.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
                document.add(new Paragraph("⚠ No se pudo cargar el logo.\n"));
            }

           
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Resumen Nutricional - NutriAI\n\n", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            
            Locale localeES = new Locale("es", "MX");
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", localeES);
            String fechaES = sdf.format(new Date());

            document.add(new Paragraph("Usuario: " + nombreUsuario));
            document.add(new Paragraph("Calorías recomendadas: " + kcal + " kcal"));
            document.add(new Paragraph("Fecha de generación: " + fechaES));
            document.add(Chunk.NEWLINE);

            
            List<Plan> planes = planRepository.findByUsuarioIdOrderByDia(usuarioId);

            if (planes == null || planes.isEmpty()) {
                document.add(new Paragraph("⚠ No hay planes registrados para este usuario.\n"));
            } else {
                String diaActual = "";

                for (Plan p : planes) {
                    if (p.getDia() != null && !p.getDia().equals(diaActual)) {
                        document.add(Chunk.NEWLINE);
                        document.add(new Paragraph("📅 " + p.getDia(),
                                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                        diaActual = p.getDia();
                    }

                   
                    String comida = p.getComida() != null ? p.getComida() : "Sin especificar";
                    String plato = p.getPlato() != null ? p.getPlato() : "No definido";

                    document.add(new Paragraph("🍽 " + comida + ": " + plato));
                }
            }

            document.close();
            out.close();

        } catch (DocumentException de) {
            de.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
