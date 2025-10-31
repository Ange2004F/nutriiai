package com.nutriia.nutriiai.model;

import jakarta.persistence.*;

@Entity
@Table(name = "planes")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 🔹 Relación con el usuario (por ahora como ID simple, se puede cambiar a @ManyToOne más adelante)
    @Column(name = "usuario_id", nullable = false)
    private int usuarioId;

    @Column(nullable = false, length = 20)
    private String dia;

    @Column(nullable = false, length = 30)
    private String comida;

    @Column(nullable = false, length = 255)
    private String plato;

    // 🔹 Constructores
    public Plan() {
    }

    public Plan(int usuarioId, String dia, String comida, String plato) {
        this.usuarioId = usuarioId;
        this.dia = dia;
        this.comida = comida;
        this.plato = plato;
    }

    // 🔹 Getters y Setters
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

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getComida() {
        return comida;
    }

    public void setComida(String comida) {
        this.comida = comida;
    }

    public String getPlato() {
        return plato;
    }

    public void setPlato(String plato) {
        this.plato = plato;
    }

    // 🔹 Método de utilidad opcional (solo para depuración)
    @Override
    public String toString() {
        return String.format(
                "Plan [usuarioId=%d, dia='%s', comida='%s', plato='%s']",
                usuarioId, dia, comida, plato
        );
    }
}
