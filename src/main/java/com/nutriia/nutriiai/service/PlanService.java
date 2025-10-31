package com.nutriia.nutriiai.service;

import com.nutriia.nutriiai.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;
    public void borrarPlanPorUsuario(int usuarioId) {
        try {
            planRepository.deleteByUsuarioId(usuarioId);
            System.out.println("🗑️ Plan del usuario " + usuarioId + " eliminado correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el plan del usuario: " + e.getMessage(), e);
        }
    }
}
