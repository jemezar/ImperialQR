package com.imperial.qr.service.calculo;

import com.imperial.qr.config.ImperialProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Principio SRP: CalculadoraTotales calcula subtotal, impuesto al consumo (8%),
 * propina voluntaria opcional (10%) y costo de envio segun RN-09.
 */
@Component
public class CalculadoraTotales {

    private final ImperialProperties properties;

    public CalculadoraTotales(ImperialProperties properties) {
        this.properties = properties;
    }

    public record LiquidacionCuenta(
        BigDecimal subtotal,
        BigDecimal impuesto,
        BigDecimal propina,
        BigDecimal costoEnvio,
        BigDecimal total
    ) {}

    public LiquidacionCuenta liquidar(BigDecimal subtotal, boolean incluirPropina, BigDecimal costoEnvio) {
        BigDecimal sub = subtotal != null ? subtotal : BigDecimal.ZERO;
        BigDecimal impPorcentaje = properties.getImpuestoConsumo();
        BigDecimal impuesto = sub.multiply(impPorcentaje).setScale(2, RoundingMode.HALF_UP);

        BigDecimal propina = BigDecimal.ZERO;
        if (incluirPropina) {
            BigDecimal propPorcentaje = properties.getPropinaSugerida();
            propina = sub.multiply(propPorcentaje).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal envio = costoEnvio != null ? costoEnvio : BigDecimal.ZERO;
        BigDecimal total = sub.add(impuesto).add(propina).add(envio).setScale(2, RoundingMode.HALF_UP);

        return new LiquidacionCuenta(sub, impuesto, propina, envio, total);
    }
}
