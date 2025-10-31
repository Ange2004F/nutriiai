package com.nutriia.nutriiai.repository;

import com.nutriia.nutriiai.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

   
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    Usuario findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    List<Usuario> findByRolIgnoreCase(String rol);
    List<Usuario> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);
    List<Usuario> findByEstadoIgnoreCase(String estado);
    List<Usuario> findByRol(String rol);
    void deleteByCorreo(String correo);
}
