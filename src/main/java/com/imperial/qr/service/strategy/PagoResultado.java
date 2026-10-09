package com.imperial.qr.service.strategy;

import java.math.BigDecimal;

public record PagoResultado(
    boolean exitoso,
    BigDecimal monto,
    String referencia,
    String mensaje
) {
    public static PagoResultado aprobado(BigDecimal monto, String referencia) {
        return new PagoResultado(true, monto, referencia, "Pago aprobado exitosamente");
    }

    public static PagoResultado rechazado(BigDecimal monto, String referencia, String motivo) {
        return new PagoResultado(false, monto, referencia, motivo);
    }
}
