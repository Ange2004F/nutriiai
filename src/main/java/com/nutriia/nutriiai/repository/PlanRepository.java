package com.nutriia.nutriiai.repository;

import com.nutriia.nutriiai.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

    
    void deleteByUsuarioId(int usuarioId);

    List<Plan> findByUsuarioIdOrderByDia(int usuarioId);
    List<Plan> findAll();
    List<Plan> findByPlatoContainingIgnoreCase(String plato);
    List<Plan> findByDiaOrderByComida(String dia);
}
