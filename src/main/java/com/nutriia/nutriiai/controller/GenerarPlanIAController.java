package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Plan;
import com.nutriia.nutriiai.repository.PlanRepository;
import com.nutriia.nutriiai.service.IAService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GenerarPlanIAController {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private IAService iaService;

    
    @Transactional
    @PostMapping(value = {"/generar-plan-ia", "/plan/generar"}, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String generarPlanIA(@org.springframework.web.bind.annotation.RequestBody String body,
                                HttpSession session,
                                HttpServletResponse response) {

        try {
            
            JSONObject jsonRequest = new JSONObject(body);
            String prompt = jsonRequest.getString("prompt");

            
            String promptIA = "Actúa como un asistente experto en nutrición general llamado NutriAI. "
                    + "Tu tarea es crear un ejemplo educativo de un plan de alimentación saludable y equilibrado, "
                    + "solo con fines informativos y sin consejos médicos. "
                    + "Debe incluir 5 días (Lunes a Viernes) con desayuno, comida y cena, usando alimentos naturales, "
                    + "con calorías, proteínas, carbohidratos y grasas estimadas. "
                    + "Usa un formato JSON estricto, sin texto adicional ni explicaciones. "
                    + "Ejemplo: {\"Lunes\":{\"Desayuno\":\"Avena con plátano (calorías: 350, proteínas: 15g, carbos: 50g, grasas: 8g)\","
                    + "\"Comida\":\"Pollo con arroz integral (calorías: 600, proteínas: 40g, carbos: 55g, grasas: 15g)\","
                    + "\"Cena\":\"Ensalada de atún (calorías: 400, proteínas: 30g, carbos: 20g, grasas: 10g)\"},"
                    + "\"Martes\":{...}}. "
                    + "Petición del usuario: " + prompt;

            
            String respuestaIA = iaService.consultarIA(promptIA);

            
            int inicio = respuestaIA.indexOf("{");
            int fin = respuestaIA.lastIndexOf("}");
            if (inicio < 0 || fin < 0 || fin <= inicio) {
                return "{\"error\":\"Formato de respuesta inválido de IA.\"}";
            }

            respuestaIA = respuestaIA.substring(inicio, fin + 1)
                    .replaceAll("(?i)<br>", "")
                    .replaceAll("(?i)```", "")
                    .replaceAll("(?i)json", "")
                    .replaceAll("\r", "")
                    .replaceAll("\n", "")
                    .trim();

            
            if (respuestaIA.startsWith("\"") && respuestaIA.endsWith("\"")) {
                respuestaIA = respuestaIA.substring(1, respuestaIA.length() - 1)
                        .replace("\\\"", "\"")
                        .replace("\\n", "")
                        .replace("\\r", "");
            }

            JSONObject planSemanal = new JSONObject(respuestaIA);

           
            if (session == null || session.getAttribute("idUsuario") == null) {
                return "{\"error\":\"Sesión no válida.\"}";
            }
            int usuarioId = (int) session.getAttribute("idUsuario");

            
            planRepository.deleteByUsuarioId(usuarioId);

           
            for (String dia : planSemanal.keySet()) {
                JSONObject comidas = planSemanal.getJSONObject(dia);

                
                String diaNormalizado = dia.substring(0, 1).toUpperCase() + dia.substring(1).toLowerCase();

                for (String tipoComida : comidas.keySet()) {
                    String tipo = tipoComida.trim().toLowerCase();
                    if (tipo.contains("desayuno")) tipo = "Desayuno";
                    else if (tipo.contains("comida")) tipo = "Comida";
                    else if (tipo.contains("cena")) tipo = "Cena";

                    Plan plan = new Plan();
                    plan.setUsuarioId(usuarioId);
                    plan.setDia(diaNormalizado);
                    plan.setComida(tipo);
                    plan.setPlato(comidas.getString(tipoComida));

                    planRepository.save(plan);
                }
            }

            System.out.println("✅ Plan generado y guardado correctamente para usuario: " + usuarioId);
            return "{\"plan\":\"Plan generado correctamente.\"}";

        } catch (JSONException e) {
            System.err.println("❌ Error al interpretar el JSON.");
            return "{\"error\":\"La IA respondió algo que no es JSON válido.\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Error inesperado al generar el plan.\"}";
        }
    }
}
