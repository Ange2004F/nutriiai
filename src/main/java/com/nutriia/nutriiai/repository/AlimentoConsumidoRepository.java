package com.nutriia.nutriiai.repository;

import com.nutriia.nutriiai.model.AlimentoConsumido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlimentoConsumidoRepository extends JpaRepository<AlimentoConsumido, Integer> {

    
    List<AlimentoConsumido> findByUsuarioIdAndFecha(int usuarioId, LocalDate fecha);

    List<AlimentoConsumido> findByUsuarioIdAndMomentoAndFecha(int usuarioId, String momento, LocalDate fecha);
}
