package com.imperial.qr.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ingrediente")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 30)
    private String unidad = "unidad";

    @Column(name = "precio_extra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioExtra = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean disponible = true;

    public Ingrediente() {}

    public Ingrediente(String nombre, String unidad, BigDecimal precioExtra, boolean disponible) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.precioExtra = precioExtra;
        this.disponible = disponible;
    }

    public Ingrediente(Long id, String nombre, String unidad, BigDecimal precioExtra, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
        this.precioExtra = precioExtra;
        this.disponible = disponible;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public BigDecimal getPrecioExtra() { return precioExtra; }
    public void setPrecioExtra(BigDecimal precioExtra) { this.precioExtra = precioExtra; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
