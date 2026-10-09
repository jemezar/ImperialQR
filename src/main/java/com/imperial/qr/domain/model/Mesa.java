package com.imperial.qr.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "mesa")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(name = "codigo_qr", nullable = false, unique = true, length = 64)
    private String codigoQr;

    @Column(nullable = false, length = 30)
    private String estado = "DISPONIBLE";

    public Mesa() {}

    public Mesa(Integer numero, Integer capacidad) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.codigoQr = UUID.randomUUID().toString();
        this.estado = "DISPONIBLE";
    }

    public Mesa(Long id, Integer numero, String codigoQr) {
        this.id = id;
        this.numero = numero;
        this.capacidad = 4;
        this.codigoQr = codigoQr;
        this.estado = "DISPONIBLE";
    }

    public Mesa(Long id, Integer numero, Integer capacidad, String codigoQr, String estado) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.codigoQr = codigoQr;
        this.estado = estado;
    }

    public void regenerarQr() {
        this.codigoQr = UUID.randomUUID().toString();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
