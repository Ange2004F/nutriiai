package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import com.nutriia.nutriiai.service.IAService;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IAService iaService;

    
    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        String correo = (String) session.getAttribute("usuario");
        if (correo == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("kcal", usuario.getKcalDiarias());
        model.addAttribute("proteinas", usuario.getProteinas());
        model.addAttribute("carbohidratos", usuario.getCarbohidratos());
        model.addAttribute("grasas", usuario.getGrasas());
        model.addAttribute("errorIA", session.getAttribute("errorIA"));

        return "perfil";
    }

    
    @PostMapping("/perfil")
    public String actualizarPerfil(HttpSession session,
                                   String correo,
                                   String sexo,
                                   int edad,
                                   double peso,
                                   double altura,
                                   double actividad,
                                   String nivelActividad,
                                   String objetivo) {

        String correoSesion = (String) session.getAttribute("usuario");
        if (correoSesion == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoSesion);
        if (usuario == null) {
            session.invalidate();
            return "redirect:/login";
        }

        usuario.setCorreo(correo);
        usuario.setSexo(sexo);
        usuario.setEdad(edad);
        usuario.setPeso(peso);
        usuario.setAltura(altura);
        usuario.setActividad(actividad);
        usuario.setNivelActividad(nivelActividad);
        usuario.setObjetivo(objetivo);

        double kcalDiarias = 0.0;
        double proteinasIA = 0.0;
        double carbohidratosIA = 0.0;
        double grasasIA = 0.0;
        String errorIA = null;

        
        if (usuario.getKcalDiarias() > 0) {
            System.out.println("✅ Usuario ya tiene kcal registradas, no se recalcula.");
            return "redirect:/perfil";
        }

        try {
            
            String prompt = String.format("""
            Eres un nutriólogo profesional certificado.
            Calcula la ingesta calórica y macronutrientes ideales del siguiente paciente:

            Sexo: %s
            Edad: %d años
            Peso: %.1f kg
            Altura: %.1f cm
            Nivel de actividad: %s
            Objetivo: %s

            Devuelve un JSON válido con los siguientes campos:
            {
              "kcal_recomendadas": número,
              "proteinas_g": número,
              "carbohidratos_g": número,
              "grasas_g": número
            }
            """, sexo, edad, peso, altura, nivelActividad, objetivo);

            
            JSONObject respuestaIA = iaService.obtenerKcalRecomendadas(prompt);
            System.out.println("🔹 Respuesta IA JSON: " + respuestaIA);

           
            double kcalIA = respuestaIA.optDouble("kcal_recomendadas", 0);
            double proteinasCalc = respuestaIA.optDouble("proteinas_g", 0);
            double carbohidratosCalc = respuestaIA.optDouble("carbohidratos_g", 0);
            double grasasCalc = respuestaIA.optDouble("grasas_g", 0);

            
            if (kcalIA > 800 && kcalIA < 6000) {
                kcalDiarias = kcalIA;
                proteinasIA = proteinasCalc;
                carbohidratosIA = carbohidratosCalc;
                grasasIA = grasasCalc;
            } else {
                kcalDiarias = calcularBMR(sexo, edad, peso, altura, actividad);
                proteinasIA = (peso * 1.8); 
                grasasIA = (kcalDiarias * 0.25) / 9; 
                carbohidratosIA = (kcalDiarias - ((proteinasIA * 4) + (grasasIA * 9))) / 4;
            }

        } catch (Exception e) {
            System.out.println("❌ Error al conectar con IA: " + e.getMessage());
            kcalDiarias = calcularBMR(sexo, edad, peso, altura, actividad);
            proteinasIA = (peso * 1.8);
            grasasIA = (kcalDiarias * 0.25) / 9;
            carbohidratosIA = (kcalDiarias - ((proteinasIA * 4) + (grasasIA * 9))) / 4;
            errorIA = "⚠️ No se pudo conectar con la IA. Se usó un cálculo estimado.";
        }

        
        usuario.setKcalDiarias((int) Math.round(kcalDiarias));
        usuario.setProteinas(proteinasIA);
        usuario.setCarbohidratos(carbohidratosIA);
        usuario.setGrasas(grasasIA);
        usuarioRepository.save(usuario);

       
        session.setAttribute("usuario", usuario.getCorreo());
        session.setAttribute("kcal_diarias", kcalDiarias);
        session.setAttribute("proteinas", proteinasIA);
        session.setAttribute("carbohidratos", carbohidratosIA);
        session.setAttribute("grasas", grasasIA);
        session.setAttribute("errorIA", errorIA);

        return "redirect:/perfil";
    }

    
    private int calcularBMR(String sexo, int edad, double peso, double altura, double actividad) {
        double bmr;
        if ("Femenino".equalsIgnoreCase(sexo)) {
            bmr = 655 + (9.6 * peso) + (1.8 * altura) - (4.7 * edad);
        } else {
            bmr = 66 + (13.7 * peso) + (5 * altura) - (6.8 * edad);
        }
        return (int) Math.round(bmr * actividad);
    }
}
