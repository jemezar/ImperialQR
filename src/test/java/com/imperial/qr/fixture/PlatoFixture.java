package com.imperial.qr.fixture;

import com.imperial.qr.domain.model.Categoria;
import com.imperial.qr.domain.model.Ingrediente;
import com.imperial.qr.domain.model.Plato;

import java.math.BigDecimal;

public class PlatoFixture {

    public static Plato arrozChino() {
        Categoria cat = new Categoria(1L, "Arroces Especiales", "Arroces chinos tradicionales", true);
        Plato p = new Plato(1L, cat, "Arroz chino especial", "Arroz frito al wok", new BigDecimal("22000"), null, true);

        Ingrediente cebolla = IngredienteFixture.cebollaRemovible();
        Ingrediente polloExtra = IngredienteFixture.polloAdicional();

        // En la receta: cebolla es removible (true) y NO adicionable (false)
        p.agregarIngredienteReceta(cebolla, BigDecimal.ONE, true, false);
        // pollo extra es NO removible (false) y adicionable (true)
        p.agregarIngredienteReceta(polloExtra, BigDecimal.ONE, false, true);

        return p;
    }
}
