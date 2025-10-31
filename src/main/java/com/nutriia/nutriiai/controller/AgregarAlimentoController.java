package com.nutriia.nutriiai.controller;

import com.nutriia.nutriiai.model.AlimentoConsumido;
import com.nutriia.nutriiai.repository.AlimentoConsumidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class AgregarAlimentoController {

    @Autowired
    private AlimentoConsumidoRepository alimentoConsumidoRepository;

    
    @GetMapping("/agregarAlimento")
    public String mostrarFormulario() {
        return "agregarAlimento";
    }

   
    @PostMapping("/agregar-alimento")
    public String agregarAlimento(
            HttpSession session,
            @RequestParam("nombre") String nombre,
            @RequestParam("tipo") String tipo,
            @RequestParam("momento") String momento,
            @RequestParam("cantidad") double cantidad,
            @RequestParam("calorias") double calorias,
            @RequestParam("proteinas") double proteinas,
            @RequestParam("carbohidratos") double carbohidratos,
            @RequestParam("grasas") double grasas) {


        if (session == null || session.getAttribute("idUsuario") == null) {
            return "redirect:/login";
        }

        int usuarioId = (int) session.getAttribute("idUsuario");

        try {
            
            AlimentoConsumido alimento = new AlimentoConsumido();
            alimento.setUsuarioId(usuarioId);
            alimento.setNombre(nombre);
            alimento.setTipo(tipo);
            alimento.setMomento(momento);
            alimento.setCantidad(cantidad);
            alimento.setCalorias(calorias);
            alimento.setProteinas(proteinas);
            alimento.setCarbohidratos(carbohidratos);
            alimento.setGrasas(grasas);
            alimento.setFecha(LocalDate.now());

            
            alimentoConsumidoRepository.save(alimento);

            
            session.setAttribute("mensaje", "✅ Alimento agregado correctamente a " + momento);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("mensaje", "❌ Error al guardar el alimento: " + e.getMessage());
        }

        return "redirect:/bienvenido";
    }
}
