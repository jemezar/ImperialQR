package com.imperial.qr.service;

import com.imperial.qr.config.ImperialProperties;
import com.imperial.qr.domain.enums.AccionModificacion;
import com.imperial.qr.domain.enums.EstadoDetalle;
import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.model.Ingrediente;
import com.imperial.qr.domain.model.Mesa;
import com.imperial.qr.domain.model.Orden;
import com.imperial.qr.domain.model.Plato;
import com.imperial.qr.dto.response.OrdenResponse;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.exception.ReglaNegocioException;
import com.imperial.qr.fixture.IngredienteFixture;
import com.imperial.qr.fixture.OrdenFixture;
import com.imperial.qr.fixture.OrdenRequestFixture;
import com.imperial.qr.fixture.PlatoFixture;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.IngredienteRepository;
import com.imperial.qr.repository.MesaRepository;
import com.imperial.qr.repository.OrdenRepository;
import com.imperial.qr.repository.PlatoRepository;
import com.imperial.qr.service.calculo.CalculadoraPrecio;
import com.imperial.qr.service.calculo.CalculadoraTotales;
import com.imperial.qr.service.impl.OrdenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceImplTest {

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private PlatoRepository platoRepository;

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Spy
    private CalculadoraPrecio calculadoraPrecio = new CalculadoraPrecio();

    @Spy
    private CalculadoraTotales calculadoraTotales = new CalculadoraTotales(new ImperialProperties());

    @Spy
    private ImperialProperties properties = new ImperialProperties();

    @Spy
    private OrdenMapper mapper = new OrdenMapper();

    @InjectMocks
    private OrdenServiceImpl ordenService;

    private Mesa mesa;
    private Plato arrozChino;
    private Ingrediente cebolla;
    private Ingrediente polloExtra;

    @BeforeEach
    void setUp() {
        mesa = new Mesa(1L, 5, UUID.randomUUID().toString());
        arrozChino = PlatoFixture.arrozChino();         // precio base 22.000
        cebolla = IngredienteFixture.cebollaRemovible();
        polloExtra = IngredienteFixture.polloAdicional(); // extra 5.000
    }

    @Test
    @DisplayName("PU-02: Calcula el precio del plato sumando los ingredientes agregados (RN-03)")
    void crearOrden_conIngredienteAdicional_sumaCostoExtra() {
        var request = OrdenRequestFixture.unPlatoConModificacion(
            arrozChino.getId(), polloExtra.getId(), AccionModificacion.AGREGAR
        );

        when(mesaRepository.findByCodigoQr(mesa.getCodigoQr())).thenReturn(Optional.of(mesa));
        when(ordenRepository.findByMesaAndEstado(mesa, EstadoOrden.ABIERTA)).thenReturn(Optional.empty());
        when(platoRepository.findById(arrozChino.getId())).thenReturn(Optional.of(arrozChino));
        when(ingredienteRepository.findById(polloExtra.getId())).thenReturn(Optional.of(polloExtra));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(i -> {
            Orden o = i.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrdenResponse respuesta = ordenService.crear(mesa.getCodigoQr(), request);

        assertNotNull(respuesta);
        assertEquals(1, respuesta.detalles().size());
        assertEquals(new BigDecimal("27000"), respuesta.detalles().get(0).precioUnitario());
        assertEquals(EstadoDetalle.RECIBIDO, respuesta.detalles().get(0).estado());
    }

    @Test
    @DisplayName("PU-04: Crear orden con QR inexistente lanza RecursoNoEncontradoException")
    void crearOrden_conQrInexistente_lanzaExcepcion() {
        var request = OrdenRequestFixture.platoSimple(1L);
        when(mesaRepository.findByCodigoQr("qr-invalido")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> ordenService.crear("qr-invalido", request));
    }

    @Test
    @DisplayName("PU-05: Plato no disponible lanza ReglaNegocioException (RN-04)")
    void crearOrden_platoNoDisponible_lanzaReglaNegocio() {
        var request = OrdenRequestFixture.platoSimple(arrozChino.getId());
        arrozChino.setDisponible(false);

        when(mesaRepository.findByCodigoQr(mesa.getCodigoQr())).thenReturn(Optional.of(mesa));
        when(platoRepository.findById(arrozChino.getId())).thenReturn(Optional.of(arrozChino));

        assertThrows(ReglaNegocioException.class, () -> ordenService.crear(mesa.getCodigoQr(), request));
    }

    @Test
    @DisplayName("PU-06: Rechaza quitar un ingrediente que no es removible (RN-02)")
    void crearOrden_quitarIngredienteNoRemovible_lanzaReglaNegocio() {
        var request = OrdenRequestFixture.unPlatoConModificacion(
            arrozChino.getId(), cebolla.getId(), AccionModificacion.QUITAR
        );
        // Marcamos cebolla como no removible
        arrozChino.getReceta().get(0).setRemovible(false);

        when(mesaRepository.findByCodigoQr(mesa.getCodigoQr())).thenReturn(Optional.of(mesa));
        when(platoRepository.findById(arrozChino.getId())).thenReturn(Optional.of(arrozChino));
        when(ingredienteRepository.findById(cebolla.getId())).thenReturn(Optional.of(cebolla));

        assertThrows(ReglaNegocioException.class, () -> ordenService.crear(mesa.getCodigoQr(), request));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    @DisplayName("PU-07: Segundo pedido de la misma mesa se agrega a la misma orden abierta (RN-01)")
    void crearOrden_segundoPedidoMismaMesa_agregaAMismaOrden() {
        Orden ordenExistente = Orden.nuevaDeMesa(mesa);
        ordenExistente.setId(50L);

        var request = OrdenRequestFixture.platoSimple(arrozChino.getId());

        when(mesaRepository.findByCodigoQr(mesa.getCodigoQr())).thenReturn(Optional.of(mesa));
        when(ordenRepository.findByMesaAndEstado(mesa, EstadoOrden.ABIERTA)).thenReturn(Optional.of(ordenExistente));
        when(platoRepository.findById(arrozChino.getId())).thenReturn(Optional.of(arrozChino));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(i -> i.getArgument(0));

        OrdenResponse res = ordenService.crear(mesa.getCodigoQr(), request);
        assertEquals(50L, res.id());
        assertEquals(1, ordenExistente.getDetalles().size());
    }

    @Test
    @DisplayName("PU-08 / PU-11: No permite cerrar la cuenta con platos pendientes (RN-08)")
    void cerrarCuenta_conPlatosPendientes_lanzaExcepcion() {
        Orden orden = OrdenFixture.abiertaConDetalle(EstadoDetalle.EN_PREPARACION);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(ReglaNegocioException.class, () -> ordenService.cerrar(1L, true));
    }
}
