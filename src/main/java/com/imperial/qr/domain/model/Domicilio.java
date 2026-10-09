package com.imperial.qr.domain.model;

import com.imperial.qr.domain.enums.EstadoDomicilio;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "domicilio")
public class Domicilio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false, unique = true)
    private Orden orden;

    @Column(nullable = false, length = 100)
    private String nombreCliente;

    @Column(nullable = false, length = 30)
    private String telefono;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(length = 255)
    private String notasDireccion;

    @Column(name = "costo_envio", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoDomicilio estado = EstadoDomicilio.SOLICITADO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domiciliario_id")
    private Usuario domiciliario;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public Domicilio() {}

    public Domicilio(Orden orden, String nombreCliente, String telefono, String direccion, String notasDireccion, BigDecimal costoEnvio) {
        this.orden = orden;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.direccion = direccion;
        this.notasDireccion = notasDireccion;
        this.costoEnvio = costoEnvio;
        this.estado = EstadoDomicilio.SOLICITADO;
        this.creadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getNotasDireccion() { return notasDireccion; }
    public void setNotasDireccion(String notasDireccion) { this.notasDireccion = notasDireccion; }
    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }
    public EstadoDomicilio getEstado() { return estado; }
    public void setEstado(EstadoDomicilio estado) { this.estado = estado; }
    public Usuario getDomiciliario() { return domiciliario; }
    public void setDomiciliario(Usuario domiciliario) { this.domiciliario = domiciliario; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
