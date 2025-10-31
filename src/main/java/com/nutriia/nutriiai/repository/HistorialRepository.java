package com.nutriia.nutriiai.repository;

import com.nutriia.nutriiai.model.Historial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Integer> {
    List<Historial> findByCorreoOrderByFechaDesc(String correo);
}
