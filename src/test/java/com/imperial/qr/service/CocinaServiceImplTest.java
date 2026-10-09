package com.imperial.qr.service;

import com.imperial.qr.domain.enums.EstadoDetalle;
import com.imperial.qr.domain.model.DetalleOrden;
import com.imperial.qr.exception.AccesoDenegadoException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.DetalleOrdenRepository;
import com.imperial.qr.service.impl.CocinaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CocinaServiceImplTest {

    @Mock
    private DetalleOrdenRepository detalleOrdenRepository;

    @Spy
    private OrdenMapper mapper = new OrdenMapper();

    @InjectMocks
    private CocinaServiceImpl cocinaService;

    @Test
    @DisplayName("PU-10: Cocinero intentando marcar ENTREGADO lanza AccesoDenegadoException (RN-06)")
    void cambiarEstado_cocineroMarcaEntregado_lanzaAccesoDenegado() {
        DetalleOrden detalle = new DetalleOrden();
        detalle.setId(10L);
        detalle.setEstado(EstadoDetalle.LISTO);

        when(detalleOrdenRepository.findById(10L)).thenReturn(Optional.of(detalle));

        assertThrows(AccesoDenegadoException.class,
            () -> cocinaService.cambiarEstado(10L, EstadoDetalle.ENTREGADO, 2L));
    }
}
