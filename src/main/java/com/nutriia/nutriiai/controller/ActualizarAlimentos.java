package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.Alimento;
import com.nutriia.nutriiai.repository.AlimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ActualizarAlimentos {

    @Autowired
    private AlimentoRepository alimentoRepository;

    @PostMapping("/actualizar-alimento")
    public String actualizarAlimento(
            @RequestParam("id") int id,
            @RequestParam("nombre") String nombre,
            @RequestParam("platillo") String platillo,
            @RequestParam("cantidad") double cantidad,
            @RequestParam("momento") String momento) {

        try {
            
            Optional<Alimento> optionalAlimento = alimentoRepository.findById(id);

            if (optionalAlimento.isPresent()) {
                Alimento alimento = optionalAlimento.get();

                
                alimento.setNombre(nombre);
                alimento.setPlatillo(platillo);
                alimento.setCantidad(cantidad);
                alimento.setMomento(momento);

                
                alimentoRepository.save(alimento);
            } else {
                System.err.println("⚠️ Alimento con ID " + id + " no encontrado.");
                return "redirect:/error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }

        return "redirect:/bienvenido";
    }
}
