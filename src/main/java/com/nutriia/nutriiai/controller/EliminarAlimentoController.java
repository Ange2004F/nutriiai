package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.repository.AlimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EliminarAlimentoController {

    @Autowired
    private AlimentoRepository alimentoRepository;

    @GetMapping("/alimentos/eliminar")
    public String eliminarAlimento(@RequestParam("id") int id) {
        try {
            if (alimentoRepository.existsById(id)) {
                alimentoRepository.deleteById(id);
                System.out.println("🗑️ Alimento con ID " + id + " eliminado correctamente.");
            } else {
                System.out.println("⚠️ No se encontró el alimento con ID " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error al eliminar el alimento: " + e.getMessage());
        }

        return "redirect:/bienvenido";
    }
}
