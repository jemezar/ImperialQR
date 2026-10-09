package com.imperial.qr.service;

import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.enums.MetodoPago;
import com.imperial.qr.domain.model.Mesa;
import com.imperial.qr.domain.model.Orden;
import com.imperial.qr.domain.model.Pago;
import com.imperial.qr.dto.request.RegistrarPagoRequest;
import com.imperial.qr.dto.response.PagoResponse;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.MesaRepository;
import com.imperial.qr.repository.OrdenRepository;
import com.imperial.qr.repository.PagoRepository;
import com.imperial.qr.service.impl.PagoServiceImpl;
import com.imperial.qr.service.strategy.PagoEfectivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Spy
    private OrdenMapper mapper = new OrdenMapper();

    @Test
    @DisplayName("PU-13: Pago parcial mantiene estado CERRADA y complementario pasa a PAGADA (RN-10)")
    void registrarPago_parcialYComplementario_pasaAPagada() {
        PagoEfectivo estrategiaEfectivo = new PagoEfectivo();
        PagoServiceImpl pagoService = new PagoServiceImpl(
            ordenRepository, pagoRepository, mesaRepository, List.of(estrategiaEfectivo), mapper
        );

        Mesa mesa = new Mesa(1L, 2, "qr-1");
        Orden orden = Orden.nuevaDeMesa(mesa);
        orden.setId(10L);
        orden.setEstado(EstadoOrden.CERRADA);
        orden.setTotal(new BigDecimal("50000.00"));

        when(ordenRepository.findById(10L)).thenReturn(Optional.of(orden));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> {
            Pago p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        // 1. Pago parcial de 30.000
        RegistrarPagoRequest req1 = new RegistrarPagoRequest(MetodoPago.EFECTIVO, new BigDecimal("30000.00"), "REC-01");
        PagoResponse res1 = pagoService.registrarPago(10L, req1);

        assertEquals(new BigDecimal("30000.00"), res1.monto());
        assertEquals(EstadoOrden.CERRADA, orden.getEstado());

        // 2. Pago complementario de 20.000
        RegistrarPagoRequest req2 = new RegistrarPagoRequest(MetodoPago.EFECTIVO, new BigDecimal("20000.00"), "REC-02");
        PagoResponse res2 = pagoService.registrarPago(10L, req2);

        assertEquals(new BigDecimal("20000.00"), res2.monto());
        assertEquals(EstadoOrden.PAGADA, orden.getEstado());
        assertEquals("DISPONIBLE", mesa.getEstado());
    }
}
