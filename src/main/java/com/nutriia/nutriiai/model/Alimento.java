package com.nutriia.nutriiai.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alimentos")
public class Alimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String comida;
    private String nombre;
    private int calorias;
    private double proteinas;
    private double carbohidratos;
    private double grasas;

    
    private String platillo;
    private double cantidad;
    private String momento;

    
    @Column(name = "usuario_id")
    private Integer usuarioId;

  
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getComida() { return comida; }
    public void setComida(String comida) { this.comida = comida; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCalorias() { return calorias; }
    public void setCalorias(int calorias) { this.calorias = calorias; }

    public double getProteinas() { return proteinas; }
    public void setProteinas(double proteinas) { this.proteinas = proteinas; }

    public double getCarbohidratos() { return carbohidratos; }
    public void setCarbohidratos(double carbohidratos) { this.carbohidratos = carbohidratos; }

    public double getGrasas() { return grasas; }
    public void setGrasas(double grasas) { this.grasas = grasas; }

    public String getPlatillo() { return platillo; }
    public void setPlatillo(String platillo) { this.platillo = platillo; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public String getMomento() { return momento; }
    public void setMomento(String momento) { this.momento = momento; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}
