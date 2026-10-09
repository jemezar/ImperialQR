package com.imperial.qr.fixture;

import com.imperial.qr.domain.enums.EstadoDetalle;
import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.model.DetalleOrden;
import com.imperial.qr.domain.model.Mesa;
import com.imperial.qr.domain.model.Orden;
import com.imperial.qr.domain.model.Plato;

import java.math.BigDecimal;
import java.util.ArrayList;

public class OrdenFixture {

    public static Orden abiertaConDetalle(EstadoDetalle estadoDetalle) {
        Mesa mesa = new Mesa(1L, 5, "uuid-mesa-5");
        Orden orden = Orden.nuevaDeMesa(mesa);
        orden.setId(1L);

        Plato plato = PlatoFixture.arrozChino();
        DetalleOrden detalle = new DetalleOrden(plato, 1, new BigDecimal("22000"), null, new ArrayList<>());
        detalle.setId(10L);
        detalle.setEstado(estadoDetalle);

        orden.agregarDetalle(detalle);
        return orden;
    }
}
