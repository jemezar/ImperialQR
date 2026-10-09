package com.imperial.qr.mapper;

import com.imperial.qr.domain.enums.AccionModificacion;
import com.imperial.qr.domain.model.*;
import com.imperial.qr.dto.response.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
public class OrdenMapper {

    public PlatoResponse toPlatoResponse(Plato plato) {
        if (plato == null) return null;

        List<IngredienteResponse> receta = (plato.getReceta() == null) ? Collections.emptyList() :
            plato.getReceta().stream()
                .map(pi -> new IngredienteResponse(
                    pi.getIngrediente().getId(),
                    pi.getIngrediente().getNombre(),
                    pi.getIngrediente().getUnidad(),
                    pi.getIngrediente().getPrecioExtra(),
                    pi.getIngrediente().isDisponible(),
                    pi.isRemovible(),
                    pi.isAdicionable(),
                    pi.getCantidad()
                ))
                .toList();

        return new PlatoResponse(
            plato.getId(),
            plato.getCategoria() != null ? plato.getCategoria().getId() : null,
            plato.getCategoria() != null ? plato.getCategoria().getNombre() : null,
            plato.getNombre(),
            plato.getDescripcion(),
            plato.getPrecioBase(),
            plato.getImagenUrl(),
            plato.isDisponible(),
            receta
        );
    }

    public CategoriaResponse toCategoriaResponse(Categoria cat) {
        if (cat == null) return null;
        List<PlatoResponse> platos = (cat.getPlatos() == null) ? Collections.emptyList() :
            cat.getPlatos().stream()
                .filter(Plato::isDisponible)
                .map(this::toPlatoResponse)
                .toList();

        return new CategoriaResponse(
            cat.getId(),
            cat.getNombre(),
            cat.getDescripcion(),
            cat.isActivo(),
            platos
        );
    }

    public IngredienteResponse toIngredienteResponse(Ingrediente ing) {
        if (ing == null) return null;
        return IngredienteResponse.simple(
            ing.getId(),
            ing.getNombre(),
            ing.getUnidad(),
            ing.getPrecioExtra(),
            ing.isDisponible()
        );
    }

    public DetalleOrdenResponse toDetalleResponse(DetalleOrden detalle) {
        if (detalle == null) return null;

        List<String> modificaciones = (detalle.getModificaciones() == null) ? Collections.emptyList() :
            detalle.getModificaciones().stream()
                .map(m -> (m.getAccion() == AccionModificacion.QUITAR ? "SIN " : "CON ") +
                          (m.getIngrediente() != null ? m.getIngrediente().getNombre() : "ingrediente") +
                          (m.getAccion() == AccionModificacion.AGREGAR && m.getCostoExtra().compareTo(BigDecimal.ZERO) > 0 ? " extra" : ""))
                .toList();

        Integer mesaNumero = (detalle.getOrden() != null && detalle.getOrden().getMesa() != null)
            ? detalle.getOrden().getMesa().getNumero() : null;

        return new DetalleOrdenResponse(
            detalle.getId(),
            detalle.getPlato() != null ? detalle.getPlato().getId() : null,
            detalle.getPlato() != null ? detalle.getPlato().getNombre() : null,
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            detalle.calcularSubtotal(),
            detalle.getEstado(),
            detalle.getObservaciones(),
            modificaciones,
            mesaNumero,
            detalle.getCreadoEn(),
            detalle.getEnPreparacionEn(),
            detalle.getListoEn(),
            detalle.getEntregadoEn()
        );
    }

    public OrdenResponse toResponse(Orden orden) {
        if (orden == null) return null;

        List<DetalleOrdenResponse> detalles = (orden.getDetalles() == null) ? Collections.emptyList() :
            orden.getDetalles().stream()
                .map(this::toDetalleResponse)
                .toList();

        BigDecimal pagado = orden.totalPagadoAprobado();
        BigDecimal totalFinal = orden.getTotal() != null && orden.getTotal().compareTo(BigDecimal.ZERO) > 0
            ? orden.getTotal() : orden.getSubtotal();
        BigDecimal saldo = totalFinal.subtract(pagado);
        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            saldo = BigDecimal.ZERO;
        }

        DomicilioResponse domResponse = orden.getDomicilio() != null ? toDomicilioResponse(orden.getDomicilio()) : null;
        EncuestaResponse encResponse = orden.getEncuesta() != null ? toEncuestaResponse(orden.getEncuesta()) : null;

        return new OrdenResponse(
            orden.getId(),
            orden.getTipo(),
            orden.getMesa() != null ? orden.getMesa().getNumero() : null,
            orden.getMesa() != null ? orden.getMesa().getId() : null,
            orden.getEstado(),
            orden.getSubtotal(),
            orden.getImpuesto(),
            orden.getPropina(),
            orden.getCostoEnvio(),
            orden.getTotal(),
            pagado,
            saldo,
            orden.getApertura(),
            orden.getCierre(),
            detalles,
            domResponse,
            encResponse
        );
    }

    public DomicilioResponse toDomicilioResponse(Domicilio dom) {
        if (dom == null) return null;
        return new DomicilioResponse(
            dom.getId(),
            dom.getOrden() != null ? dom.getOrden().getId() : null,
            dom.getNombreCliente(),
            dom.getTelefono(),
            dom.getDireccion(),
            dom.getNotasDireccion(),
            dom.getCostoEnvio(),
            dom.getEstado(),
            dom.getDomiciliario() != null ? dom.getDomiciliario().getId() : null,
            dom.getDomiciliario() != null ? dom.getDomiciliario().getNombre() : null,
            dom.getCreadoEn()
        );
    }

    public EncuestaResponse toEncuestaResponse(Encuesta enc) {
        if (enc == null) return null;
        return new EncuestaResponse(
            enc.getId(),
            enc.getOrden() != null ? enc.getOrden().getId() : null,
            enc.getCalComida(),
            enc.getCalServicio(),
            enc.getCalGeneral(),
            enc.getComentario(),
            enc.getCreadoEn()
        );
    }

    public PagoResponse toPagoResponse(Pago pago) {
        if (pago == null) return null;
        Orden o = pago.getOrden();
        BigDecimal totalOrden = o != null ? o.getTotal() : BigDecimal.ZERO;
        BigDecimal pagado = o != null ? o.totalPagadoAprobado() : BigDecimal.ZERO;
        BigDecimal saldo = totalOrden.subtract(pagado);
        if (saldo.compareTo(BigDecimal.ZERO) < 0) saldo = BigDecimal.ZERO;

        return new PagoResponse(
            pago.getId(),
            o != null ? o.getId() : null,
            pago.getMetodo(),
            pago.getMonto(),
            pago.getReferencia(),
            pago.getEstado(),
            pago.getFecha(),
            totalOrden,
            pagado,
            saldo
        );
    }

    public UsuarioResponse toUsuarioResponse(Usuario u) {
        if (u == null) return null;
        return new UsuarioResponse(
            u.getId(),
            u.getNombre(),
            u.getEmail(),
            u.getRol(),
            u.isActivo(),
            u.getCreadoEn()
        );
    }
}
