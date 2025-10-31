package com.nutriia.nutriiai.service;

import com.nutriia.nutriiai.model.Usuario;
import com.nutriia.nutriiai.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

  
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

   
    public Usuario registrarUsuario(Usuario usuario) {
        
        if (usuarioRepository.findByCorreo(usuario.getCorreo()) != null) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        try {
            
            usuario.setPassword(encriptarSHA256(usuario.getPassword()));
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }

    
    public boolean login(String correo, String password) {
        Usuario user = usuarioRepository.findByCorreo(correo);

        if (user == null) {
            return false;
        }

        try {
            String passwordHash = encriptarSHA256(password);
            return user.getPassword().equals(passwordHash);
        } catch (Exception e) {
            return false;
        }
    }

 
    public boolean eliminarUsuario(int id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
    private String encriptarSHA256(String texto) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(texto.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
