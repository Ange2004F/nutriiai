package com.nutriia.nutriiai.repository;

import com.nutriia.nutriiai.model.Progreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProgresoRepository extends JpaRepository<Progreso, Integer> {

  
    List<Progreso> findByUsuarioCorreoOrderByFechaDesc(String correo);
    List<Progreso> findAllByOrderByFechaDesc();
}
