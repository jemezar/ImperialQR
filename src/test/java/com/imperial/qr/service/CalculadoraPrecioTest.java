package com.imperial.qr.service.calculo;

import com.imperial.qr.domain.enums.AccionModificacion;
import com.imperial.qr.domain.model.Ingrediente;
import com.imperial.qr.domain.model.ModificacionDetalle;
import com.imperial.qr.domain.model.Plato;
import com.imperial.qr.fixture.IngredienteFixture;
import com.imperial.qr.fixture.PlatoFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraPrecioTest {

    private CalculadoraPrecio calculadoraPrecio;
    private Plato arrozChino;

    @BeforeEach
    void setUp() {
        calculadoraPrecio = new CalculadoraPrecio();
        arrozChino = PlatoFixture.arrozChino(); // base 22.000
    }

    @Test
    @DisplayName("PU-01: Plato sin modificaciones mantiene precio base")
    void platoSinModificaciones_precioBase() {
        BigDecimal resultado = calculadoraPrecio.calcular(arrozChino, Collections.emptyList());
        assertEquals(new BigDecimal("22000"), resultado);
    }

    @Test
    @DisplayName("PU-02: Plato con ingrediente agregado suma costo extra (RN-03)")
    void platoConIngredienteAgregado_sumaCostoExtra() {
        Ingrediente polloExtra = IngredienteFixture.polloAdicional(); // 5.000
        ModificacionDetalle mod = new ModificacionDetalle(polloExtra, AccionModificacion.AGREGAR, polloExtra.getPrecioExtra());

        BigDecimal resultado = calculadoraPrecio.calcular(arrozChino, List.of(mod));
        assertEquals(new BigDecimal("27000"), resultado);
    }

    @Test
    @DisplayName("PU-03: Plato con ingrediente quitado no reduce el precio (RN-03)")
    void platoConIngredienteQuitado_precioSinCambio() {
        Ingrediente cebolla = IngredienteFixture.cebollaRemovible();
        ModificacionDetalle mod = new ModificacionDetalle(cebolla, AccionModificacion.QUITAR, BigDecimal.ZERO);

        BigDecimal resultado = calculadoraPrecio.calcular(arrozChino, List.of(mod));
        assertEquals(new BigDecimal("22000"), resultado);
    }
}
