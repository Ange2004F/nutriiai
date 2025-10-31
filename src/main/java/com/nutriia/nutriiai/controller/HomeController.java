package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.AlimentoConsumido;
import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.AlimentoConsumidoRepository;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import com.nutriia.nutriiai.service.IAService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlimentoConsumidoRepository alimentoConsumidoRepository;

    @Autowired
    private IAService iaService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/bienvenido")
    public String bienvenido(HttpSession session, Model model) {
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

        
        JSONObject kcalRecomendadas = (JSONObject) session.getAttribute("kcalRecomendadas");

        if (kcalRecomendadas == null) {

            
            if (usuario.getKcalDiarias() > 0) {
                kcalRecomendadas = new JSONObject();

                double kcal = usuario.getKcalDiarias();
                double prote = (usuario.getProteinas() != 0) ? usuario.getProteinas() : (usuario.getPeso() * 1.8);
                double grasa = (usuario.getGrasas() != 0) ? usuario.getGrasas() : (kcal * 0.25) / 9;
                double carb = (usuario.getCarbohidratos() != 0) ? usuario.getCarbohidratos() :
                        (kcal - ((prote * 4) + (grasa * 9))) / 4;

                kcalRecomendadas.put("kcal_recomendadas", kcal);
                kcalRecomendadas.put("proteinas_g", Math.round(prote));
                kcalRecomendadas.put("carbohidratos_g", Math.round(carb));
                kcalRecomendadas.put("grasas_g", Math.round(grasa));
                kcalRecomendadas.put("razonamiento", "Datos estimados con base en el perfil del usuario.");
            } else {
  
                String datosUsuario = String.format(
                        "Sexo: %s, Edad: %d años, Peso: %.2f kg, Altura: %.2f cm, Nivel de actividad: %s, Objetivo: %s.",
                        usuario.getSexo(),
                        usuario.getEdad(),
                        usuario.getPeso(),
                        usuario.getAltura(),
                        usuario.getNivelActividad(),
                        usuario.getObjetivo()
                );

                kcalRecomendadas = iaService.obtenerKcalRecomendadas(datosUsuario);

                
                if (!kcalRecomendadas.has("kcal_recomendadas")) {
                    kcalRecomendadas.put("kcal_recomendadas", 0);
                    kcalRecomendadas.put("proteinas_g", 0);
                    kcalRecomendadas.put("carbohidratos_g", 0);
                    kcalRecomendadas.put("grasas_g", 0);
                    kcalRecomendadas.put("razonamiento", "⚠️ No se pudo calcular correctamente las calorías recomendadas.");
                }

                
                session.setAttribute("kcalRecomendadas", kcalRecomendadas);

                try {
                    usuario.setKcalDiarias(kcalRecomendadas.getDouble("kcal_recomendadas"));
                    usuario.setProteinas(kcalRecomendadas.getDouble("proteinas_g"));
                    usuario.setCarbohidratos(kcalRecomendadas.getDouble("carbohidratos_g"));
                    usuario.setGrasas(kcalRecomendadas.getDouble("grasas_g"));
                    usuarioRepository.save(usuario);
                } catch (Exception ignored) {
                    
                }

            }
        }

        model.addAttribute("kcalRecomendadas", kcalRecomendadas);

        try {
            LocalDate hoy = LocalDate.now();

            
            List<AlimentoConsumido> alimentos = alimentoConsumidoRepository.findByUsuarioIdAndFecha(usuario.getId(), hoy);

            double totalCalorias = 0, totalProteinas = 0, totalCarbohidratos = 0, totalGrasas = 0;

            
            Map<String, List<Map<String, Object>>> comidas = new HashMap<>();
            comidas.put("desayuno", new ArrayList<>());
            comidas.put("comida", new ArrayList<>());
            comidas.put("cena", new ArrayList<>());

            for (AlimentoConsumido a : alimentos) {
                totalCalorias += a.getCalorias();
                totalProteinas += a.getProteinas();
                totalCarbohidratos += a.getCarbohidratos();
                totalGrasas += a.getGrasas();

                Map<String, Object> item = new HashMap<>();
                item.put("nombre", a.getNombre());
                item.put("cantidad", a.getCantidad());
                item.put("calorias", a.getCalorias());
                item.put("proteinas", a.getProteinas());
                item.put("carbohidratos", a.getCarbohidratos());
                item.put("grasas", a.getGrasas());

                String momento = a.getMomento() != null ? a.getMomento().toLowerCase() : "otro";
                comidas.computeIfAbsent(momento, k -> new ArrayList<>()).add(item);
            }

            model.addAttribute("comidas", comidas);
            model.addAttribute("kcalConsumidas", totalCalorias);
            model.addAttribute("protConsumidas", totalProteinas);
            model.addAttribute("carbConsumidas", totalCarbohidratos);
            model.addAttribute("grasConsumidas", totalGrasas);

            
            if (session.getAttribute("mensaje") != null) {
                model.addAttribute("mensaje", session.getAttribute("mensaje"));
                session.removeAttribute("mensaje"); 
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "⚠️ Error cargando alimentos: " + e.getMessage());
        }

        return "bienvenido";
    }
}
