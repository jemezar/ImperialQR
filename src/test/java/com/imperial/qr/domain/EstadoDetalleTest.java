package com.imperial.qr.domain;

import com.imperial.qr.domain.enums.EstadoDetalle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoDetalleTest {

    @ParameterizedTest
    @CsvSource({
        "RECIBIDO,EN_PREPARACION",
        "EN_PREPARACION,LISTO",
        "LISTO,ENTREGADO",
        "RECIBIDO,CANCELADO"
    })
    @DisplayName("PU-09: Transiciones válidas según RN-05")
    void transicionesValidas(EstadoDetalle origen, EstadoDetalle destino) {
        assertTrue(origen.puedePasarA(destino));
    }

    @ParameterizedTest
    @CsvSource({
        "RECIBIDO,LISTO",
        "LISTO,EN_PREPARACION",
        "ENTREGADO,CANCELADO",
        "EN_PREPARACION,CANCELADO",
        "CANCELADO,RECIBIDO"
    })
    @DisplayName("PU-09: Transiciones inválidas rechazadas según RN-05")
    void transicionesInvalidas(EstadoDetalle origen, EstadoDetalle destino) {
        assertFalse(origen.puedePasarA(destino));
    }
}
