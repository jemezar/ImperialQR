package com.imperial.qr.service;

import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.model.Orden;
import com.imperial.qr.dto.request.RegistrarEncuestaRequest;
import com.imperial.qr.exception.ConflictoNegocioException;
import com.imperial.qr.exception.ReglaNegocioException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.EncuestaRepository;
import com.imperial.qr.repository.OrdenRepository;
import com.imperial.qr.service.impl.EncuestaServiceImpl;
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
class EncuestaServiceImplTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private EncuestaRepository encuestaRepository;

    @Spy
    private OrdenMapper mapper = new OrdenMapper();

    @InjectMocks
    private EncuestaServiceImpl encuestaService;

    @Test
    @DisplayName("PU-14: Encuesta antes de pagar lanza ReglaNegocioException (RN-11)")
    void registrarEncuesta_ordenNoPagada_lanzaReglaNegocio() {
        Orden orden = new Orden();
        orden.setId(1L);
        orden.setEstado(EstadoOrden.CERRADA); // No está PAGADA

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        RegistrarEncuestaRequest req = new RegistrarEncuestaRequest(5, 5, 5, "Excelente comida");
        assertThrows(ReglaNegocioException.class, () -> encuestaService.registrarEncuesta(1L, req));
    }

    @Test
    @DisplayName("PU-14: Encuesta repetida para la misma orden lanza ConflictoNegocioException (RN-11)")
    void registrarEncuesta_repetida_lanzaConflicto() {
        Orden orden = new Orden();
        orden.setId(1L);
        orden.setEstado(EstadoOrden.PAGADA);

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(encuestaRepository.existsByOrdenId(1L)).thenReturn(true);

        RegistrarEncuestaRequest req = new RegistrarEncuestaRequest(5, 5, 5, "Excelente");
        assertThrows(ConflictoNegocioException.class, () -> encuestaService.registrarEncuesta(1L, req));
    }
}
