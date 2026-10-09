package com.imperial.qr.service.impl;

import com.imperial.qr.domain.enums.EstadoDomicilio;
import com.imperial.qr.domain.enums.Rol;
import com.imperial.qr.domain.model.*;
import com.imperial.qr.dto.request.CrearDomicilioRequest;
import com.imperial.qr.dto.request.ItemOrdenRequest;
import com.imperial.qr.dto.response.DomicilioResponse;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.exception.ReglaNegocioException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.*;
import com.imperial.qr.service.DomicilioService;
import com.imperial.qr.service.calculo.CalculadoraPrecio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DomicilioServiceImpl implements DomicilioService {

    private final DomicilioRepository domicilioRepository;
    private final OrdenRepository ordenRepository;
    private final PlatoRepository platoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CalculadoraPrecio calculadoraPrecio;
    private final OrdenMapper mapper;

    public DomicilioServiceImpl(DomicilioRepository domicilioRepository,
                                OrdenRepository ordenRepository,
                                PlatoRepository platoRepository,
                                IngredienteRepository ingredienteRepository,
                                UsuarioRepository usuarioRepository,
                                CalculadoraPrecio calculadoraPrecio,
                                OrdenMapper mapper) {
        this.domicilioRepository = domicilioRepository;
        this.ordenRepository = ordenRepository;
        this.platoRepository = platoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.calculadoraPrecio = calculadoraPrecio;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DomicilioResponse crearDomicilio(CrearDomicilioRequest request) {
        // RN-12: Un pedido a domicilio no se asocia a una mesa y exige nombre, teléfono, dirección y costo de envío.
        if (request.nombreCliente() == null || request.nombreCliente().isBlank()) {
            throw new ReglaNegocioException("El nombre del cliente es obligatorio para domicilios (RN-12)");
        }
        if (request.telefono() == null || request.telefono().isBlank()) {
            throw new ReglaNegocioException("El teléfono es obligatorio para domicilios (RN-12)");
        }
        if (request.direccion() == null || request.direccion().isBlank()) {
            throw new ReglaNegocioException("La dirección es obligatoria para domicilios (RN-12)");
        }

        Orden orden = Orden.nuevaDeDomicilio(request.costoEnvio());

        if (request.items() == null || request.items().isEmpty()) {
            throw new ReglaNegocioException("El pedido a domicilio debe incluir al menos un plato");
        }

        for (ItemOrdenRequest item : request.items()) {
            Plato plato = platoRepository.findById(item.platoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado con ID: " + item.platoId()));
            plato.validarDisponible();

            List<ModificacionDetalle> mods = new ArrayList<>();
            if (item.modificaciones() != null) {
                for (var modReq : item.modificaciones()) {
                    Ingrediente ing = ingredienteRepository.findById(modReq.ingredienteId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Ingrediente no encontrado con ID: " + modReq.ingredienteId()));
                    mods.add(plato.validarYCrearModificacion(ing, modReq.accion()));
                }
            }

            BigDecimal precioUnitario = calculadoraPrecio.calcular(plato, mods);
            DetalleOrden detalle = new DetalleOrden(plato, item.cantidad(), precioUnitario, item.observaciones(), mods);
            orden.agregarDetalle(detalle);
        }

        Orden ordenGuardada = ordenRepository.save(orden);

        Domicilio domicilio = new Domicilio(
            ordenGuardada,
            request.nombreCliente(),
            request.telefono(),
            request.direccion(),
            request.notasDireccion(),
            request.costoEnvio()
        );

        Domicilio domicilioGuardado = domicilioRepository.save(domicilio);
        ordenGuardada.setDomicilio(domicilioGuardado);

        return mapper.toDomicilioResponse(domicilioGuardado);
    }

    @Override
    @Transactional
    public DomicilioResponse actualizarDomicilio(Long domicilioId, Long domiciliarioId, EstadoDomicilio nuevoEstado) {
        Domicilio domicilio = domicilioRepository.findById(domicilioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Domicilio no encontrado con ID: " + domicilioId));

        if (domiciliarioId != null) {
            Usuario domiciliario = usuarioRepository.findById(domiciliarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Domiciliario no encontrado con ID: " + domiciliarioId));
            if (domiciliario.getRol() != Rol.DOMICILIARIO && domiciliario.getRol() != Rol.ADMIN) {
                throw new ReglaNegocioException("El usuario asignado no tiene rol DOMICILIARIO");
            }
            domicilio.setDomiciliario(domiciliario);
            if (domicilio.getEstado() == EstadoDomicilio.SOLICITADO) {
                domicilio.setEstado(EstadoDomicilio.ASIGNADO);
            }
        }

        if (nuevoEstado != null) {
            domicilio.setEstado(nuevoEstado);
        }

        Domicilio guardado = domicilioRepository.save(domicilio);
        return mapper.toDomicilioResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public DomicilioResponse obtenerPorId(Long domicilioId) {
        Domicilio dom = domicilioRepository.findById(domicilioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Domicilio no encontrado con ID: " + domicilioId));
        return mapper.toDomicilioResponse(dom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomicilioResponse> listar(EstadoDomicilio estado) {
        if (estado != null) {
            return domicilioRepository.findByEstado(estado).stream()
                .map(mapper::toDomicilioResponse)
                .toList();
        }
        return domicilioRepository.findAll().stream()
            .map(mapper::toDomicilioResponse)
            .toList();
    }
}
