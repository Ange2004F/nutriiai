package com.nutriia.nutriiai.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;


@Service
public class IAService {

  
    private static final String OPENROUTER_API_KEY =
            "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";

  
    private static final String MODEL_TEXT = "meta-llama/llama-3.1-8b-instruct";
    private static final String MODEL_VISION = "meta-llama/llama-3.2-11b-vision-instruct";
    private static final String ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";

    public JSONObject obtenerKcalRecomendadas(String datosUsuario) {
        try {
            String prompt = """
                Eres un nutriólogo profesional certificado en salud y nutrición.
                Analiza cuidadosamente los siguientes datos del usuario:

                """ + datosUsuario + """

                Calcula su ingesta calórica diaria ideal aplicando la fórmula Mifflin-St Jeor
                (o Harris-Benedict si es más apropiada), teniendo en cuenta sexo, edad, peso,
                altura, nivel de actividad y objetivo (bajar, mantener o ganar masa muscular).

                Luego calcula también los macronutrientes aproximados:
                - Proteínas: entre 1.6 y 2.2 g/kg
                - Grasas: 25–30 % de las calorías
                - Carbohidratos: el resto

                Devuelve únicamente un JSON válido con este formato:
                {
                  "kcal_recomendadas": 0,
                  "proteinas_g": 0,
                  "carbohidratos_g": 0,
                  "grasas_g": 0,
                  "razonamiento": "Texto breve explicando el cálculo"
                }
                """;

            JSONObject body = new JSONObject();
            body.put("model", MODEL_TEXT);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", prompt));
            body.put("messages", messages);

            String respuesta = enviarPeticion(body);
            return extraerJson(respuesta);

        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("error", "❌ Error al conectar con OpenRouter: " + e.getMessage());
            return error;
        }
    }

public String consultarIA(String pregunta) {
    try {
        if (pregunta == null || pregunta.isBlank())
            return "⚠️ Por favor, escribe una consulta o saludo.";

       
        String promptBase = """
            Eres **NutriAI**, un asistente nutricional profesional, empático y experto en salud humana,
            alimentación y bienestar. Hablas siempre en un tono amable, claro y profesional,
            sin tecnicismos innecesarios.

            Tu rol es actuar como un **nutriólogo clínico certificado**, con amplia experiencia en:
            - Nutrición general y deportiva
            - Bienestar digestivo y metabólico
            - Hábitos alimenticios saludables
            - Educación nutricional para distintos objetivos (pérdida de peso, masa muscular, salud digestiva, etc.)

            🎯 Tu objetivo:
            Analizar las preguntas del usuario (aunque estén mal escritas o incompletas),
            **corregir su ortografía y gramática de forma automática**, interpretar su intención
            y responder con un lenguaje claro, ordenado y profesional.

            ⚖️ Normas:
            - Si el usuario escribe con errores, responde el texto como si estuviera bien redactado.
            - Nunca digas groserías ni sugerencias dañinas o fuera de contexto (como fumar, beber, etc.).
            - Evita repetir información innecesaria.
            - Si la pregunta no es nutricional, responde con educación pero aclara tu especialidad.
            - Siempre escribe en español correcto, con buena puntuación y estructura.
            - Presenta la respuesta en párrafos bien formateados y, si es útil, en listas numeradas o con viñetas.

            Ejemplo de tono:
            Usuario: ola ke komer para subir musculo
            NutriAI:  
            Claro 😊. Para aumentar masa muscular necesitas una dieta con un ligero superávit calórico
            y una adecuada cantidad de proteínas de calidad.  
            - Incluye pollo, pescado, huevos, legumbres y lácteos bajos en grasa.  
            - Añade carbohidratos complejos como avena, arroz integral y papas.  
            - No olvides frutas, verduras y suficiente agua.  
            También te recomiendo realizar entrenamiento de fuerza 3 a 5 veces por semana.

            Ahora analiza la siguiente consulta del usuario y respóndele como NutriAI:
            """ + pregunta;

        
        JSONObject body = new JSONObject();
        body.put("model", MODEL_TEXT);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", promptBase));
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", pregunta));
        body.put("messages", messages);

       
        String respuesta = enviarPeticion(body);

        if (respuesta == null || respuesta.isBlank())
            return "⚠️ No pude generar una respuesta en este momento. Intenta de nuevo.";

    
        respuesta = respuesta
                .replace("\n\n", "<br><br>")
                .replace("\n", "<br>")
                .replaceAll("\\*\\*", "")
                .trim();

        return respuesta;

    } catch (Exception e) {
        e.printStackTrace();
        return "❌ Error al consultar la IA: " + e.getMessage();
    }

}
    public String analizarFoto(MultipartFile fotoComida, String momento) {
        try {
            if (fotoComida == null || fotoComida.isEmpty())
                return "⚠️ No se proporcionó ninguna imagen.";

            byte[] bytes = fotoComida.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);

            JSONObject body = new JSONObject();
            body.put("model", MODEL_VISION);

            JSONArray messages = new JSONArray();
            JSONObject user = new JSONObject();
            user.put("role", "user");

            JSONArray content = new JSONArray();
            content.put(new JSONObject().put("type", "text").put("text",
                    "Analiza esta imagen de comida. Describe qué platillo es, "
                    + "sus posibles ingredientes y estima los macronutrientes "
                    + "(calorías, proteínas, carbohidratos y grasas). "
                    + "Momento del día: " + momento + "."));
            content.put(new JSONObject().put("type", "image_url")
                    .put("image_url", "data:image/jpeg;base64," + base64));

            user.put("content", content);
            messages.put(user);
            body.put("messages", messages);

            return enviarPeticion(body);

        } catch (Exception e) {
            return "❌ Error al analizar la imagen: " + e.getMessage();
        }
    }

    private String enviarPeticion(JSONObject body) throws Exception {
        URL url = new URL(ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + OPENROUTER_API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("HTTP-Referer", "https://nutriia.com");
        conn.setRequestProperty("X-Title", "NutriIA");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes());
            os.flush();
        }

        int status = conn.getResponseCode();
        InputStream inputStream = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }

        String respuestaJson = sb.toString();
        System.out.println("💬 [NutriIA DEBUG] Respuesta completa de IA (" + status + "): " + respuestaJson);

        if (status == 401)
            return "❌ Error 401: Clave API inválida o sin permisos.";
        if (status == 429)
            return "⚠️ Límite de uso de la API alcanzado. Intenta más tarde.";
        if (status >= 500)
            return "❌ Error del servidor de IA. (" + status + ")";

        if (respuestaJson.isBlank())
            return "⚠️ No se recibió respuesta del servidor.";

        JSONObject json = new JSONObject(respuestaJson);
        JSONArray choices = json.optJSONArray("choices");

        if (choices == null || choices.isEmpty()) {
            return "⚠️ La IA no devolvió respuesta válida.";
        }

        JSONObject firstChoice = choices.getJSONObject(0);
        String content = "";

        if (firstChoice.has("message")) {
            content = firstChoice.getJSONObject("message").optString("content", "");
        } else if (firstChoice.has("text")) {
            content = firstChoice.optString("text", "");
        }

        return content.isBlank() ? "⚠️ La IA respondió vacío o en formato inesperado." : content.trim();
    }


    private JSONObject extraerJson(String texto) {
        try {
            int start = texto.indexOf('{');
            int end = texto.lastIndexOf('}');
            if (start == -1 || end == -1) {
                JSONObject err = new JSONObject();
                err.put("error", "⚠️ Formato inesperado en la respuesta IA.");
                err.put("texto", texto);
                return err;
            }
            String jsonText = texto.substring(start, end + 1);
            return new JSONObject(jsonText);
        } catch (Exception e) {
            JSONObject err = new JSONObject();
            err.put("error", "❌ No se pudo interpretar JSON: " + e.getMessage());
            err.put("texto", texto);
            return err;
        }
    }
}
