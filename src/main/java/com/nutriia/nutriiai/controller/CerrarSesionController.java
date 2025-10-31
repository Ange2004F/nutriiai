package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.service.SesionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CerrarSesionController {

    @Autowired
    private SesionService sesionService;

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        sesionService.cerrarSesion(session);
        return "redirect:/login";
    }
}
