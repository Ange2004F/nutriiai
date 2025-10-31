package com.nutriia.nutriiai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(name = "nombres", nullable = false)
    private String nombres;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ingresar un correo válido")
    @Column(unique = true, nullable = false)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    private String sexo;

    @Min(value = 1, message = "La edad debe ser mayor a 0")
    @Max(value = 120, message = "La edad no puede ser mayor a 120 años")
    private int edad;

    @Min(value = 1, message = "El peso debe ser mayor a 0")
    private double peso;

    @Min(value = 0, message = "La altura no puede ser negativa")
    private double altura;

    @Column(name = "nivel_actividad")
    private String nivelActividad;

    private String objetivo;

    @Column(name = "actividad")
    private double actividad;

    
    @Column(name = "kcal_diarias", columnDefinition = "DOUBLE DEFAULT 0")
    private double kcalDiarias;

   
    @Column(name = "proteinas", columnDefinition = "DOUBLE DEFAULT 0")
    private double proteinas;

    @Column(name = "carbohidratos", columnDefinition = "DOUBLE DEFAULT 0")
    private double carbohidratos;

    @Column(name = "grasas", columnDefinition = "DOUBLE DEFAULT 0")
    private double grasas;

    
    @NotBlank(message = "El rol no puede estar vacío")
    @Column(nullable = false)
    private String rol = "Paciente"; 

   
    @NotBlank(message = "El estado no puede estar vacío")
    @Column(nullable = false)
    private String estado = "Activo"; 

    
    public Usuario() {}

    
    public Usuario(String nombres, String apellidos, String correo, String password, String rol) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
    }

   
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }

    public String getNivelActividad() { return nivelActividad; }
    public void setNivelActividad(String nivelActividad) { this.nivelActividad = nivelActividad; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public double getActividad() { return actividad; }
    public void setActividad(double actividad) { this.actividad = actividad; }

    public double getKcalDiarias() { return kcalDiarias; }
    public void setKcalDiarias(double kcalDiarias) { this.kcalDiarias = kcalDiarias; }

    public double getProteinas() { return proteinas; }
    public void setProteinas(double proteinas) { this.proteinas = proteinas; }

    public double getCarbohidratos() { return carbohidratos; }
    public void setCarbohidratos(double carbohidratos) { this.carbohidratos = carbohidratos; }

    public double getGrasas() { return grasas; }
    public void setGrasas(double grasas) { this.grasas = grasas; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
