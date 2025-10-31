package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Plan;
import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.PlanRepository;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Optional;

@Controller
public class GuardarPlanController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanRepository planRepository;

    @PostMapping(value = "/guardar-plan", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String guardarPlan(String plan, HttpSession session) {
        try {
            String correoUsuario = (String) session.getAttribute("usuario");

            if (correoUsuario == null) {
                return "❌ No hay sesión activa.";
            }

            if (plan == null || plan.isEmpty()) {
                return "⚠️ No se recibió ningún plan.";
            }

            
            Usuario usuario = usuarioRepository.findByCorreo(correoUsuario);
            if (usuario == null) {
                return "❌ Usuario no encontrado.";
            }

            int usuarioId = usuario.getId();

            
            planRepository.deleteByUsuarioId(usuarioId);

            
            String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
            String[] comidas = {"Desayuno", "Comida", "Cena"};

            BufferedReader reader = new BufferedReader(new StringReader(plan));
            String linea;
            String diaActual = null;

            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();

              
                for (String d : dias) {
                    if (linea.toLowerCase().startsWith(d.toLowerCase())) {
                        diaActual = d;
                        break;
                    }
                }

               
                for (String comida : comidas) {
                    if (diaActual != null && linea.toLowerCase().startsWith(comida.toLowerCase())) {
                        String plato = linea.substring(comida.length()).trim().replaceAll("^[\\-:\\s]+", "");

                        Plan nuevoPlan = new Plan();
                        nuevoPlan.setUsuarioId(usuarioId);
                        nuevoPlan.setDia(diaActual);
                        nuevoPlan.setComida(comida);
                        nuevoPlan.setPlato(plato);

                        planRepository.save(nuevoPlan);
                        break;
                    }
                }
            }

            System.out.println("📄 Plan guardado correctamente para el usuario: " + correoUsuario);
            return "✅ Plan guardado correctamente.";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error al guardar el plan: " + e.getMessage();
        }
    }
}
