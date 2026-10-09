package com.imperial.qr.service.calculo;

import com.imperial.qr.domain.enums.AccionModificacion;
import com.imperial.qr.domain.model.ModificacionDetalle;
import com.imperial.qr.domain.model.Plato;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Principio SRP: CalculadoraPrecio se encarga unicamente del calculo del precio unitario
 * de un plato considerando su precio base y las modificaciones (RN-03).
 */
@Component
public class CalculadoraPrecio {

    public BigDecimal calcular(Plato plato, List<ModificacionDetalle> mods) {
        if (plato == null) {
            return BigDecimal.ZERO;
        }
        if (mods == null || mods.isEmpty()) {
            return plato.getPrecioBase();
        }

        BigDecimal extras = mods.stream()
            .filter(m -> m.getAccion() == AccionModificacion.AGREGAR)
            .map(ModificacionDetalle::getCostoExtra)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Quitar un ingrediente no reduce el precio (RN-03)
        return plato.getPrecioBase().add(extras);
    }
}
