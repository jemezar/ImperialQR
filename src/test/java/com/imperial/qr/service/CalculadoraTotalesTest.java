package com.imperial.qr.service.calculo;

import com.imperial.qr.config.ImperialProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraTotalesTest {

    @Test
    @DisplayName("PU-12: Subtotal con impuesto del 8%, propina voluntaria del 10% y envio calcula total exacto (RN-09)")
    void liquidar_conImpuestoYPropina_calculaTotalCorrecto() {
        ImperialProperties props = new ImperialProperties();
        props.setImpuestoConsumo(new BigDecimal("0.08"));
        props.setPropinaSugerida(new BigDecimal("0.10"));
        CalculadoraTotales calculadora = new CalculadoraTotales(props);

        BigDecimal subtotal = new BigDecimal("100000.00");
        var res = calculadora.liquidar(subtotal, true, new BigDecimal("5000.00"));

        assertEquals(new BigDecimal("100000.00"), res.subtotal());
        assertEquals(new BigDecimal("8000.00"), res.impuesto());
        assertEquals(new BigDecimal("10000.00"), res.propina());
        assertEquals(new BigDecimal("5000.00"), res.costoEnvio());
        assertEquals(new BigDecimal("123000.00"), res.total());
    }
}
