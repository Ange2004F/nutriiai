package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Plan;
import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.PlanRepository;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NutriologoPlanController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanRepository planRepository;

    // 🔹 Mostrar lista de pacientes del nutriólogo
    // (Se cambió la URL para evitar conflicto con NutriologoController)
    @GetMapping("/nutriologo/planes/lista")
    public String mostrarPacientesConPlanes(HttpSession session, Model model) {
        String correo = (String) session.getAttribute("usuario");

        if (correo == null) {
            return "redirect:/login";
        }

        Usuario nutriologo = usuarioRepository.findByCorreo(correo);
        if (nutriologo == null || !"Nutriologo".equalsIgnoreCase(nutriologo.getRol())) {
            return "redirect:/error";
        }

        List<Usuario> pacientes = usuarioRepository.findByRol("Paciente");
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("nombreNutriologo", nutriologo.getNombres() + " " + nutriologo.getApellidos());
        return "nutriologo_planes";
    }

    // 🔹 Ver planes de un paciente específico
    @GetMapping("/nutriologo/planes/ver")
    public String verPlanesDePaciente(@RequestParam("id") int idUsuario, Model model) {
        List<Plan> planes = planRepository.findByUsuarioIdOrderByDia(idUsuario);
        Usuario paciente = usuarioRepository.findById(idUsuario).orElse(null);

        model.addAttribute("paciente", paciente);
        model.addAttribute("planes", planes);
        return "nutriologo_planes_ver";
    }

    // 🔹 Mostrar formulario para crear o asignar un plan
    @GetMapping("/nutriologo/planes/crear")
    public String mostrarFormularioCrearPlan(HttpSession session, Model model) {
        String correo = (String) session.getAttribute("usuario");

        if (correo == null) {
            return "redirect:/login";
        }

        Usuario nutriologo = usuarioRepository.findByCorreo(correo);
        if (nutriologo == null || !"Nutriologo".equalsIgnoreCase(nutriologo.getRol())) {
            return "redirect:/error";
        }

        List<Usuario> pacientes = usuarioRepository.findByRol("Paciente");
        model.addAttribute("pacientes", pacientes);
        return "nutriologo_planes_crear";
    }

    // 🔹 Guardar el plan del paciente
    @org.springframework.web.bind.annotation.PostMapping("/nutriologo/planes/guardar")
    public String guardarPlan(
            @org.springframework.web.bind.annotation.RequestParam("idUsuario") int idUsuario,
            @org.springframework.web.bind.annotation.RequestParam("dia") String dia,
            @org.springframework.web.bind.annotation.RequestParam("comida") String comida,
            @org.springframework.web.bind.annotation.RequestParam("plato") String plato,
            HttpSession session,
            Model model) {

        String correo = (String) session.getAttribute("usuario");
        if (correo == null) {
            return "redirect:/login";
        }

        Usuario nutriologo = usuarioRepository.findByCorreo(correo);
        if (nutriologo == null || !"Nutriologo".equalsIgnoreCase(nutriologo.getRol())) {
            return "redirect:/error";
        }

        // Crear nuevo plan
        Plan plan = new Plan();
        plan.setUsuarioId(idUsuario);
        plan.setDia(dia);
        plan.setComida(comida);
        plan.setPlato(plato);

        planRepository.save(plan);

        // Mensaje de éxito
        model.addAttribute("exito", "✅ Plan guardado correctamente para el paciente.");
        List<Usuario> pacientes = usuarioRepository.findByRol("Paciente");
        model.addAttribute("pacientes", pacientes);

        return "nutriologo_planes_crear";
    }
}
