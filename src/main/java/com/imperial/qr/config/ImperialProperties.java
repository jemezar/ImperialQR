package com.imperial.qr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "imperial")
public class ImperialProperties {

    /**
     * Impuesto al consumo por defecto: 8 % (0.08)
     */
    private BigDecimal impuestoConsumo = new BigDecimal("0.08");

    /**
     * Propina sugerida voluntaria por defecto: 10 % (0.10)
     */
    private BigDecimal propinaSugerida = new BigDecimal("0.10");

    /**
     * Costo de envío base por defecto para domicilios
     */
    private BigDecimal costoEnvioBase = new BigDecimal("5000.00");

    public BigDecimal getImpuestoConsumo() { return impuestoConsumo; }
    public void setImpuestoConsumo(BigDecimal impuestoConsumo) { this.impuestoConsumo = impuestoConsumo; }
    public BigDecimal getPropinaSugerida() { return propinaSugerida; }
    public void setPropinaSugerida(BigDecimal propinaSugerida) { this.propinaSugerida = propinaSugerida; }
    public BigDecimal getCostoEnvioBase() { return costoEnvioBase; }
    public void setCostoEnvioBase(BigDecimal costoEnvioBase) { this.costoEnvioBase = costoEnvioBase; }
}
