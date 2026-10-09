package com.imperial.qr.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "encuesta")
public class Encuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false, unique = true)
    private Orden orden;

    @Column(name = "cal_comida", nullable = false)
    private Integer calComida;

    @Column(name = "cal_servicio", nullable = false)
    private Integer calServicio;

    @Column(name = "cal_general", nullable = false)
    private Integer calGeneral;

    @Column(length = 500)
    private String comentario;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public Encuesta() {}

    public Encuesta(Orden orden, Integer calComida, Integer calServicio, Integer calGeneral, String comentario) {
        this.orden = orden;
        this.calComida = calComida;
        this.calServicio = calServicio;
        this.calGeneral = calGeneral;
        this.comentario = comentario;
        this.creadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }
    public Integer getCalComida() { return calComida; }
    public void setCalComida(Integer calComida) { this.calComida = calComida; }
    public Integer getCalServicio() { return calServicio; }
    public void setCalServicio(Integer calServicio) { this.calServicio = calServicio; }
    public Integer getCalGeneral() { return calGeneral; }
    public void setCalGeneral(Integer calGeneral) { this.calGeneral = calGeneral; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
