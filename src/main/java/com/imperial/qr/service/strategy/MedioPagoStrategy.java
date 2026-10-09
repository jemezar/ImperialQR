package com.imperial.qr.service.strategy;

import com.imperial.qr.domain.enums.MetodoPago;
import com.imperial.qr.domain.model.Orden;

import java.math.BigDecimal;

/**
 * Principio OCP / LSP: Interfaz de estrategia para procesamiento de pagos.
 */
public interface MedioPagoStrategy {
    MetodoPago metodo();
    PagoResultado procesar(Orden orden, BigDecimal monto, String referencia);
}
