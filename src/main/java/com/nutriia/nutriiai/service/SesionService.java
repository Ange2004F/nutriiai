package com.nutriia.nutriiai.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SesionService {
    public void cerrarSesion(HttpSession session) {
        if (session != null) {
            System.out.println("🔒 Cerrando sesión del usuario: " + session.getAttribute("usuario"));
            session.invalidate();
        }
    }
}
