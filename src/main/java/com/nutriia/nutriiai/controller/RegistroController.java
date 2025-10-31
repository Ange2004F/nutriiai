package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.security.MessageDigest;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    
    @PostMapping("/registro")
    public String registrarUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            Model model) {

       
        if (result.hasErrors()) {
            model.addAttribute("error", "⚠️ Revisa los campos: hay datos inválidos o incompletos.");
            return "registro";
        }

        
        if (usuarioRepository.findByCorreo(usuario.getCorreo()) != null) {
            model.addAttribute("error", "⚠️ El correo ya está en uso. Por favor, elige otro.");
            return "registro";
        }

        try {
            
            if (usuario.getEdad() <= 0 || usuario.getPeso() <= 0 || usuario.getAltura() <= 0) {
                model.addAttribute("error", "⚠️ Edad, peso o altura deben ser mayores que 0.");
                return "registro";
            }

            
            String sexo = usuario.getSexo();
            String sexoFinal = switch (sexo.toUpperCase()) {
                case "M" -> "Masculino";
                case "F" -> "Femenino";
                default -> "Otro";
            };
            usuario.setSexo(sexoFinal);

           
            usuario.setPassword(encriptarSHA256(usuario.getPassword()));

            
            double peso = usuario.getPeso();
            double altura = usuario.getAltura();
            int edad = usuario.getEdad();
            String nivel = usuario.getNivelActividad();

            
            double tmb;
            if (sexoFinal.equalsIgnoreCase("Masculino")) {
                tmb = 66.47 + (13.75 * peso) + (5.0 * altura) - (6.75 * edad);
            } else {
                tmb = 655.1 + (9.56 * peso) + (1.85 * altura) - (4.68 * edad);
            }

            
            double factorActividad = switch (nivel.toLowerCase()) {
                case "ligero" -> 1.375;
                case "moderado" -> 1.55;
                case "activo" -> 1.725;
                case "muy activo" -> 1.9;
                case "sedentario" -> 1.2;
                default -> 1.2;
            };

         
            double kcal = tmb * factorActividad;

           
            double proteinas = (kcal * 0.20) / 4;
            double carbohidratos = (kcal * 0.50) / 4;
            double grasas = (kcal * 0.30) / 9;

           
            usuario.setKcalDiarias(kcal);
            usuario.setProteinas(proteinas);
            usuario.setCarbohidratos(carbohidratos);
            usuario.setGrasas(grasas);
            

            
            usuarioRepository.save(usuario);

            System.out.println("✅ Usuario registrado correctamente:");
            System.out.println("Nombre: " + usuario.getNombres() + " " + usuario.getApellidos());
            System.out.println("Correo: " + usuario.getCorreo());
            System.out.println("Sexo: " + usuario.getSexo());
            System.out.println("Edad: " + usuario.getEdad() + " años");
            System.out.println("Peso: " + usuario.getPeso() + " kg");
            System.out.println("Altura: " + usuario.getAltura() + " cm");
            System.out.println("Nivel actividad: " + usuario.getNivelActividad());
            System.out.println("Objetivo: " + usuario.getObjetivo());
            System.out.println("Kcal: " + usuario.getKcalDiarias());
            System.out.println("Proteínas: " + usuario.getProteinas());
            System.out.println("Carbohidratos: " + usuario.getCarbohidratos());
            System.out.println("Grasas: " + usuario.getGrasas());
            System.out.println("Password (hash): " + usuario.getPassword());

            model.addAttribute("mensaje", "✅ Registro exitoso. Ahora puedes iniciar sesión.");
            return "registro";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "⚠️ Error en el registro: " + e.getMessage());
            return "registro";
        }
    }

   
    private String encriptarSHA256(String texto) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(texto.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
