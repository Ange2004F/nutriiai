package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import java.security.MessageDigest;
import java.util.regex.Pattern;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping({"/login", "/usuarios/login"})
    public String mostrarLogin(Model model) {
        model.addAttribute("usuario", new Usuario()); 
        return "login";
    }

    @PostMapping({"/login", "/usuarios/login"})
    public ModelAndView login(
            @Valid @RequestParam("correo") String correo,
            @Valid @RequestParam("password") String password,
            HttpSession session) { 

        ModelAndView mv = new ModelAndView();

        
        if (correo == null || password == null || correo.trim().isEmpty() || password.trim().isEmpty()) {
            mv.addObject("error", "⚠️ Todos los campos son obligatorios.");
            mv.addObject("usuario", new Usuario()); 
            mv.setViewName("login");
            return mv;
        }

        String correoTrim = correo.trim();
        String passwordTrim = password.trim();

        try {
            Usuario usuario = usuarioRepository.findByCorreo(correoTrim);

            if (usuario == null) {
                mv.addObject("error", "⚠️ Correo o contraseña incorrectos.");
                mv.addObject("usuario", new Usuario()); 
                mv.setViewName("login");
                return mv;
            }

            String storedPassword = usuario.getPassword();

            
            if (isLikelySHA256Hex(storedPassword)) {
                String inputHash = encriptarSHA256(passwordTrim);
                if (!inputHash.equalsIgnoreCase(storedPassword)) {
                    mv.addObject("error", "⚠️ Correo o contraseña incorrectos.");
                    mv.addObject("usuario", new Usuario()); 
                    mv.setViewName("login");
                    return mv;
                }
            } else if (!passwordTrim.equals(storedPassword)) {
                mv.addObject("error", "⚠️ Correo o contraseña incorrectos.");
                mv.addObject("usuario", new Usuario()); 
                mv.setViewName("login");
                return mv;
            }

            
if (usuario.getKcalDiarias() == 0) {

                double peso = usuario.getPeso();
                double altura = usuario.getAltura();
                int edad = usuario.getEdad();
                String nivel = usuario.getNivelActividad();
                String sexo = usuario.getSexo();

                
                double tmb;
                if (sexo.equalsIgnoreCase("Masculino")) {
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

                
usuario.setKcalDiarias((double) Math.round(kcal));
usuario.setProteinas((double) Math.round(proteinas));
usuario.setCarbohidratos((double) Math.round(carbohidratos));
usuario.setGrasas((double) Math.round(grasas));

usuarioRepository.save(usuario); 


            }

            
            String rol = (usuario.getRol() != null && !usuario.getRol().isEmpty())
                    ? usuario.getRol()
                    : "Paciente";

            if (correoTrim.equalsIgnoreCase("admin@nutriAI.com")) rol = "Administrador";
            if (correoTrim.equalsIgnoreCase("nutri@nutriAI.com")) rol = "Nutriologo";

            
            completarSesionYRedirigir(session, mv, usuario.getId(), usuario.getNombres(),
                    usuario.getApellidos(), correoTrim, rol);

        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "⚠️ Error en el login: " + e.getMessage());
            mv.addObject("usuario", new Usuario()); 
            mv.setViewName("login");
        }

        return mv;
    }

    private void completarSesionYRedirigir(HttpSession session, ModelAndView mv,
                                           int usuarioId, String nombres, String apellidos,
                                           String correo, String rol) {

        String nombreCompleto = ((nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "")).trim();
        session.setAttribute("idUsuario", usuarioId);
        session.setAttribute("nombre", nombreCompleto);
        session.setAttribute("usuario", correo);
        session.setAttribute("rol", rol); 

        switch (rol.toLowerCase()) {
            case "administrador":
                mv.setViewName("redirect:/admin");
                break;
            case "nutriologo":
                mv.setViewName("redirect:/nutriologo");
                break;
            default:
                mv.setViewName("redirect:/bienvenido");
                break;
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

    private boolean isLikelySHA256Hex(String s) {
        if (s == null) return false;
        return Pattern.matches("^[0-9a-fA-F]{64}$", s.trim());
    }
}
