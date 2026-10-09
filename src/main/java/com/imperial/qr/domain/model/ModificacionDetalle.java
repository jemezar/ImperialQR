package com.imperial.qr.domain.model;

import com.imperial.qr.domain.enums.AccionModificacion;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "modificacion_detalle")
public class ModificacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_id", nullable = false)
    private DetalleOrden detalleOrden;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccionModificacion accion;

    @Column(name = "costo_extra", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoExtra = BigDecimal.ZERO;

    public ModificacionDetalle() {}

    public ModificacionDetalle(Ingrediente ingrediente, AccionModificacion accion, BigDecimal costoExtra) {
        this.ingrediente = ingrediente;
        this.accion = accion;
        this.costoExtra = costoExtra;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DetalleOrden getDetalleOrden() { return detalleOrden; }
    public void setDetalleOrden(DetalleOrden detalleOrden) { this.detalleOrden = detalleOrden; }
    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    public AccionModificacion getAccion() { return accion; }
    public void setAccion(AccionModificacion accion) { this.accion = accion; }
    public BigDecimal getCostoExtra() { return costoExtra; }
    public void setCostoExtra(BigDecimal costoExtra) { this.costoExtra = costoExtra; }
}
