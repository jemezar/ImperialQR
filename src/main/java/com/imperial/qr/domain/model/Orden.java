package com.imperial.qr.domain.model;

import com.imperial.qr.domain.enums.EstadoDetalle;
import com.imperial.qr.domain.enums.EstadoOrden;
import com.imperial.qr.domain.enums.TipoOrden;
import com.imperial.qr.exception.ReglaNegocioException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoOrden tipo = TipoOrden.MESA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado = EstadoOrden.ABIERTA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "apertura", nullable = false)
    private LocalDateTime apertura = LocalDateTime.now();

    @Column(name = "cierre")
    private LocalDateTime cierre;

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal propina = BigDecimal.ZERO;

    @Column(name = "costo_envio", precision = 12, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();

    @OneToOne(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private Domicilio domicilio;

    @OneToOne(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private Encuesta encuesta;

    public Orden() {}

    public static Orden nuevaDeMesa(Mesa mesa) {
        Orden o = new Orden();
        o.tipo = TipoOrden.MESA;
        o.mesa = mesa;
        o.estado = EstadoOrden.ABIERTA;
        o.apertura = LocalDateTime.now();
        return o;
    }

    public static Orden nuevaDeDomicilio(BigDecimal costoEnvio) {
        Orden o = new Orden();
        o.tipo = TipoOrden.DOMICILIO;
        o.mesa = null;
        o.estado = EstadoOrden.ABIERTA;
        o.costoEnvio = costoEnvio != null ? costoEnvio : BigDecimal.ZERO;
        o.apertura = LocalDateTime.now();
        return o;
    }

    public void agregarDetalle(DetalleOrden detalle) {
        if (this.estado != EstadoOrden.ABIERTA) {
            throw new ReglaNegocioException("No se pueden adicionar platos a una orden en estado " + this.estado + " (RN-07)");
        }
        detalle.setOrden(this);
        this.detalles.add(detalle);
        recalcularSubtotal();
    }

    public void recalcularSubtotal() {
        this.subtotal = this.detalles.stream()
            .map(DetalleOrden::calcularSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * RN-08: El administrador solo puede cerrar una orden si todos sus platos están en estado ENTREGADO o CANCELADO.
     */
    public void validarPuedeCerrarse() {
        if (this.estado != EstadoOrden.ABIERTA) {
            throw new ReglaNegocioException("La orden ya se encuentra en estado " + this.estado);
        }
        boolean tienePendientes = this.detalles.stream().anyMatch(d ->
            d.getEstado() != EstadoDetalle.ENTREGADO && d.getEstado() != EstadoDetalle.CANCELADO
        );
        if (tienePendientes) {
            throw new ReglaNegocioException("No se puede cerrar la orden: tiene platos que aún no han sido entregados o cancelados (RN-08)");
        }
    }

    public void cerrar(BigDecimal subtotal, BigDecimal impuesto, BigDecimal propina, BigDecimal costoEnvio, BigDecimal total) {
        validarPuedeCerrarse();
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.propina = propina;
        this.costoEnvio = costoEnvio;
        this.total = total;
        this.estado = EstadoOrden.CERRADA;
        this.cierre = LocalDateTime.now();
    }

    public BigDecimal totalPagadoAprobado() {
        return this.pagos.stream()
            .filter(p -> p.getEstado() == com.imperial.qr.domain.enums.EstadoPago.APROBADO)
            .map(Pago::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoOrden getTipo() { return tipo; }
    public void setTipo(TipoOrden tipo) { this.tipo = tipo; }
    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public LocalDateTime getApertura() { return apertura; }
    public void setApertura(LocalDateTime apertura) { this.apertura = apertura; }
    public LocalDateTime getCierre() { return cierre; }
    public void setCierre(LocalDateTime cierre) { this.cierre = cierre; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getImpuesto() { return impuesto; }
    public void setImpuesto(BigDecimal impuesto) { this.impuesto = impuesto; }
    public BigDecimal getPropina() { return propina; }
    public void setPropina(BigDecimal propina) { this.propina = propina; }
    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }
    public List<Pago> getPagos() { return pagos; }
    public void setPagos(List<Pago> pagos) { this.pagos = pagos; }
    public Domicilio getDomicilio() { return domicilio; }
    public void setDomicilio(Domicilio domicilio) { this.domicilio = domicilio; }
    public Encuesta getEncuesta() { return encuesta; }
    public void setEncuesta(Encuesta encuesta) { this.encuesta = encuesta; }
}
