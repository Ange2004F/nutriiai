package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.util.Optional;

@Controller
public class ConfiguracionController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/configuracion")
    public String mostrarConfiguracion(HttpSession session, Model model) {
        if (session == null || session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("mensaje", session.getAttribute("mensaje"));
        session.removeAttribute("mensaje");
        return "configuracion";
    }

    @PostMapping("/configuracion")
    public String procesarConfiguracion(
            HttpSession session,
            @RequestParam("accion") String accion,
            @RequestParam(required = false) String actual,
            @RequestParam(required = false) String nueva,
            @RequestParam(required = false) String confirmacion,
            @RequestParam(required = false) String idioma,
            @RequestParam(required = false, name = "correoNuevo") String correoNuevo,
            @RequestParam(required = false, name = "estado") String estado
    ) {
        if (session == null || session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        String correo = (String) session.getAttribute("usuario");
        Optional<Usuario> optionalUsuario = Optional.ofNullable(usuarioRepository.findByCorreo(correo));

        if (optionalUsuario.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        Usuario usuario = optionalUsuario.get();

        try {
            switch (accion.toLowerCase()) {

                case "cambiar":
                    if (!encriptarSHA256(actual).equals(usuario.getPassword())) {
                        session.setAttribute("mensaje", "❌ Contraseña actual incorrecta.");
                        return "redirect:/configuracion";
                    }

                    usuario.setPassword(encriptarSHA256(nueva));
                    usuarioRepository.save(usuario);

                    session.setAttribute("mensaje", "✅ Contraseña actualizada con éxito.");
                    return "redirect:/configuracion";

                case "eliminar":
                    if (!encriptarSHA256(confirmacion).equals(usuario.getPassword())) {
                        session.setAttribute("mensaje", "❌ Contraseña incorrecta. No se eliminó la cuenta.");
                        return "redirect:/configuracion";
                    }

                    usuarioRepository.delete(usuario);
                    session.invalidate();
                    return "redirect:/index";

                case "idioma":
                    session.setAttribute("idioma", idioma);
                    session.setAttribute("mensaje", "✅ Idioma actualizado a: " + idioma.toUpperCase());
                    return "redirect:/configuracion";

                case "correo":
                    usuario.setCorreo(correoNuevo);
                    usuarioRepository.save(usuario);

                    session.setAttribute("usuario", correoNuevo);
                    session.setAttribute("mensaje", "✅ Correo actualizado con éxito.");
                    return "redirect:/configuracion";

                case "notificaciones":
                    boolean activado = "on".equals(estado);
          
                    usuarioRepository.save(usuario);

                    session.setAttribute("mensaje", "✅ Preferencia de notificaciones actualizada.");
                    return "redirect:/configuracion";
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("mensaje", "❌ Ocurrió un error: " + e.getMessage());
            return "redirect:/configuracion";
        }

        session.setAttribute("mensaje", "✅ (Simulado) Cambios aplicados correctamente.");
        return "redirect:/configuracion";
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
