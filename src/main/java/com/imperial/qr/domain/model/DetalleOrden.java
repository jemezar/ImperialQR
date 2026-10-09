package com.imperial.qr.domain.model;

import com.imperial.qr.domain.enums.EstadoDetalle;
import com.imperial.qr.exception.ReglaNegocioException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "detalle_orden")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plato_id", nullable = false)
    private Plato plato;

    @Column(nullable = false)
    private Integer cantidad = 1;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoDetalle estado = EstadoDetalle.RECIBIDO;

    @Column(length = 255)
    private String observaciones;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(name = "en_preparacion_en")
    private LocalDateTime enPreparacionEn;

    @Column(name = "listo_en")
    private LocalDateTime listoEn;

    @Column(name = "entregado_en")
    private LocalDateTime entregadoEn;

    @Column(name = "cocinero_id")
    private Long cocineroId;

    @Column(name = "mesero_id")
    private Long meseroId;

    @OneToMany(mappedBy = "detalleOrden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModificacionDetalle> modificaciones = new ArrayList<>();

    public DetalleOrden() {}

    public DetalleOrden(Plato plato, Integer cantidad, BigDecimal precioUnitario, String observaciones, List<ModificacionDetalle> modificaciones) {
        this.plato = plato;
        this.cantidad = cantidad != null ? cantidad : 1;
        this.precioUnitario = precioUnitario;
        this.observaciones = observaciones;
        this.estado = EstadoDetalle.RECIBIDO;
        this.creadoEn = LocalDateTime.now();
        if (modificaciones != null) {
            for (ModificacionDetalle m : modificaciones) {
                m.setDetalleOrden(this);
                this.modificaciones.add(m);
            }
        }
    }

    public void cambiarEstado(EstadoDetalle nuevoEstado, Long usuarioId) {
        if (!this.estado.puedePasarA(nuevoEstado)) {
            throw new ReglaNegocioException("Transición no válida de " + this.estado + " a " + nuevoEstado + " (RN-05)");
        }
        this.estado = nuevoEstado;
        LocalDateTime ahora = LocalDateTime.now();
        switch (nuevoEstado) {
            case EN_PREPARACION -> {
                this.enPreparacionEn = ahora;
                this.cocineroId = usuarioId;
            }
            case LISTO -> this.listoEn = ahora;
            case ENTREGADO -> {
                this.entregadoEn = ahora;
                this.meseroId = usuarioId;
            }
            default -> {}
        }
    }

    public void cancelar() {
        if (this.estado != EstadoDetalle.RECIBIDO) {
            throw new ReglaNegocioException("Solo se pueden cancelar platos en estado RECIBIDO (RN-05)");
        }
        this.estado = EstadoDetalle.CANCELADO;
    }

    public BigDecimal calcularSubtotal() {
        if (this.estado == EstadoDetalle.CANCELADO) {
            return BigDecimal.ZERO;
        }
        return this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }
    public Plato getPlato() { return plato; }
    public void setPlato(Plato plato) { this.plato = plato; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public EstadoDetalle getEstado() { return estado; }
    public void setEstado(EstadoDetalle estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
    public LocalDateTime getEnPreparacionEn() { return enPreparacionEn; }
    public void setEnPreparacionEn(LocalDateTime enPreparacionEn) { this.enPreparacionEn = enPreparacionEn; }
    public LocalDateTime getListoEn() { return listoEn; }
    public void setListoEn(LocalDateTime listoEn) { this.listoEn = listoEn; }
    public LocalDateTime getEntregadoEn() { return entregadoEn; }
    public void setEntregadoEn(LocalDateTime entregadoEn) { this.entregadoEn = entregadoEn; }
    public Long getCocineroId() { return cocineroId; }
    public void setCocineroId(Long cocineroId) { this.cocineroId = cocineroId; }
    public Long getMeseroId() { return meseroId; }
    public void setMeseroId(Long meseroId) { this.meseroId = meseroId; }
    public List<ModificacionDetalle> getModificaciones() { return modificaciones; }
    public void setModificaciones(List<ModificacionDetalle> modificaciones) { this.modificaciones = modificaciones; }
}
