package com.imperial.qr.service.strategy;

import com.imperial.qr.domain.enums.MetodoPago;
import com.imperial.qr.domain.model.Orden;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PagoTarjeta implements MedioPagoStrategy {

    @Override
    public MetodoPago metodo() {
        return MetodoPago.TARJETA;
    }

    @Override
    public PagoResultado procesar(Orden orden, BigDecimal monto, String referencia) {
        String ref = (referencia != null && !referencia.isBlank()) ? referencia : "CARD-AUTH-" + UUID.randomUUID().toString().substring(0, 8);
        return PagoResultado.aprobado(monto, ref);
    }
}
