package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Plan;
import com.nutriia.nutriiai.model.Progreso;
import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.PlanRepository;
import com.nutriia.nutriiai.repository.ProgresoRepository;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NutriologoController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private ProgresoRepository progresoRepository;

    
    @GetMapping("/nutriologo")
    public String panelNutriologo(HttpSession session, Model model) {

        Object usuario = session.getAttribute("usuario");
        Object rol = session.getAttribute("rol");

        if (usuario == null || rol == null || !rol.toString().equalsIgnoreCase("Nutriologo")) {
            return "redirect:/login";
        }

        model.addAttribute("titulo", "Panel del Nutriólogo");
        model.addAttribute("mensaje", "Bienvenido al Panel de Nutriólogo 🩺");
        model.addAttribute("nombre", session.getAttribute("nombre"));
        model.addAttribute("correo", session.getAttribute("usuario"));
        model.addAttribute("rol", rol.toString());

        return "nutriologo";
    }

    
    @GetMapping("/nutriologo/pacientes")
    public String verPacientes(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        Object rol = session.getAttribute("rol");

        if (usuario == null || rol == null || !rol.toString().equalsIgnoreCase("Nutriologo")) {
            return "redirect:/login";
        }

        try {
           
            List<Usuario> pacientes = usuarioRepository.findByRolIgnoreCase("Paciente");

            model.addAttribute("pacientes", pacientes);
            model.addAttribute("titulo", "Lista de Pacientes");
            model.addAttribute("mensaje", "Pacientes registrados en NutriAI 🧍‍♂️🧍‍♀️");
            model.addAttribute("nombre", session.getAttribute("nombre"));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "⚠️ Error al obtener la lista de pacientes: " + e.getMessage());
        }

        return "nutriologo_pacientes";
    }

    
    @GetMapping("/nutriologo/planes")
    public String verPlanes(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        Object rol = session.getAttribute("rol");

        if (usuario == null || rol == null || !rol.toString().equalsIgnoreCase("Nutriologo")) {
            return "redirect:/login";
        }

        try {
            List<Plan> planes = planRepository.findAll();

            model.addAttribute("planes", planes);
            model.addAttribute("titulo", "Planes de Alimentación");
            model.addAttribute("mensaje", "Planes personalizados creados por el nutriólogo 🍽️");
            model.addAttribute("nombre", session.getAttribute("nombre"));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "⚠️ Error al cargar los planes: " + e.getMessage());
        }

        return "nutriologo_planes";
    }

    
    @GetMapping("/nutriologo/progreso")
    public String verProgreso(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        Object rol = session.getAttribute("rol");

        if (usuario == null || rol == null || !rol.toString().equalsIgnoreCase("Nutriologo")) {
            return "redirect:/login";
        }

        try {
            List<Progreso> progreso = progresoRepository.findAllByOrderByFechaDesc();

            model.addAttribute("progreso", progreso);
            model.addAttribute("titulo", "Progreso de Pacientes");
            model.addAttribute("mensaje", "Seguimiento y evolución de los pacientes 📈");
            model.addAttribute("nombre", session.getAttribute("nombre"));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "⚠️ Error al cargar el progreso: " + e.getMessage());
        }

        return "nutriologo_progreso";
    }
}
