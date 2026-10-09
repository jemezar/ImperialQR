package com.imperial.qr.domain.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "plato_ingrediente")
@IdClass(PlatoIngredienteId.class)
public class PlatoIngrediente {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plato_id", nullable = false)
    private Plato plato;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Column(precision = 10, scale = 2)
    private BigDecimal cantidad = BigDecimal.ONE;

    @Column(nullable = false)
    private boolean removible = true;

    @Column(nullable = false)
    private boolean adicionable = true;

    public PlatoIngrediente() {}

    public PlatoIngrediente(Plato plato, Ingrediente ingrediente, BigDecimal cantidad, boolean removible, boolean adicionable) {
        this.plato = plato;
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
        this.removible = removible;
        this.adicionable = adicionable;
    }

    public Plato getPlato() { return plato; }
    public void setPlato(Plato plato) { this.plato = plato; }
    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public boolean isRemovible() { return removible; }
    public void setRemovible(boolean removible) { this.removible = removible; }
    public boolean isAdicionable() { return adicionable; }
    public void setAdicionable(boolean adicionable) { this.adicionable = adicionable; }
}
