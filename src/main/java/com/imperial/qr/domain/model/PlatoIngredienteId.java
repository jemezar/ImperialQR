package com.imperial.qr.domain.model;

import java.io.Serializable;
import java.util.Objects;

public class PlatoIngredienteId implements Serializable {
    private Long plato;
    private Long ingrediente;

    public PlatoIngredienteId() {}

    public PlatoIngredienteId(Long plato, Long ingrediente) {
        this.plato = plato;
        this.ingrediente = ingrediente;
    }

    public Long getPlato() { return plato; }
    public void setPlato(Long plato) { this.plato = plato; }
    public Long getIngrediente() { return ingrediente; }
    public void setIngrediente(Long ingrediente) { this.ingrediente = ingrediente; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlatoIngredienteId that)) return false;
        return Objects.equals(plato, that.plato) && Objects.equals(ingrediente, that.ingrediente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plato, ingrediente);
    }
}
