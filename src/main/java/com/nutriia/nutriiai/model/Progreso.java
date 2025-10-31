package com.nutriia.nutriiai.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "progreso")
public class Progreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

 
    @Column(name = "usuario_id", nullable = false)
    private int usuarioId;

  
    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;

   
    @Column(nullable = false)
    private LocalDate fecha;

    
    @Column(name = "peso_actual", nullable = false)
    private double pesoActual;

   
    @Column(name = "kcal_consumidas", nullable = false)
    private double kcalConsumidas;

   
    @Column(length = 255)
    private String nota;

   
    public Progreso() {}

 
    public Progreso(int usuarioId, LocalDate fecha, double pesoActual, double kcalConsumidas, String nota) {
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.pesoActual = pesoActual;
        this.kcalConsumidas = kcalConsumidas;
        this.nota = nota;
    }

   
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getPesoActual() {
        return pesoActual;
    }

    public void setPesoActual(double pesoActual) {
        this.pesoActual = pesoActual;
    }

    public double getKcalConsumidas() {
        return kcalConsumidas;
    }

    public void setKcalConsumidas(double kcalConsumidas) {
        this.kcalConsumidas = kcalConsumidas;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
}
