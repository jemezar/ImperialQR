package com.imperial.qr.service;

import com.imperial.qr.dto.request.CrearDomicilioRequest;
import com.imperial.qr.dto.request.ItemOrdenRequest;
import com.imperial.qr.exception.ReglaNegocioException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.DomicilioRepository;
import com.imperial.qr.repository.IngredienteRepository;
import com.imperial.qr.repository.OrdenRepository;
import com.imperial.qr.repository.PlatoRepository;
import com.imperial.qr.repository.UsuarioRepository;
import com.imperial.qr.service.calculo.CalculadoraPrecio;
import com.imperial.qr.service.impl.DomicilioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DomicilioServiceImplTest {

    @Mock private DomicilioRepository domicilioRepository;
    @Mock private OrdenRepository ordenRepository;
    @Mock private PlatoRepository platoRepository;
    @Mock private IngredienteRepository ingredienteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CalculadoraPrecio calculadoraPrecio;
    @Spy private OrdenMapper mapper = new OrdenMapper();

    @InjectMocks
    private DomicilioServiceImpl domicilioService;

    @Test
    @DisplayName("PU-15: Domicilio sin dirección lanza ReglaNegocioException (RN-12)")
    void crearDomicilio_sinDireccion_lanzaReglaNegocio() {
        ItemOrdenRequest item = new ItemOrdenRequest(1L, 1, null, List.of());
        CrearDomicilioRequest req = new CrearDomicilioRequest(
            "Juan Perez", "3001234567", "", "Apto 201", new BigDecimal("5000"), List.of(item)
        );

        assertThrows(ReglaNegocioException.class, () -> domicilioService.crearDomicilio(req));
    }
}
