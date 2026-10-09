package com.imperial.qr.fixture;

import com.imperial.qr.domain.model.Ingrediente;

import java.math.BigDecimal;

public class IngredienteFixture {

    public static Ingrediente cebollaRemovible() {
        return new Ingrediente(7L, "Cebolla blanca", "porcion", BigDecimal.ZERO, true);
    }

    public static Ingrediente polloAdicional() {
        return new Ingrediente(12L, "Pollo extra", "porcion", new BigDecimal("5000"), true);
    }
}
