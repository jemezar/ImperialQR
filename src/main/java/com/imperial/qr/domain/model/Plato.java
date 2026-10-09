package com.imperial.qr.domain.model;

import com.imperial.qr.domain.enums.AccionModificacion;
import com.imperial.qr.exception.ReglaNegocioException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plato")
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "precio_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(length = 255)
    private String imagenUrl;

    @Column(nullable = false)
    private boolean disponible = true;

    @OneToMany(mappedBy = "plato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlatoIngrediente> receta = new ArrayList<>();

    public Plato() {}

    public Plato(Long id, Categoria categoria, String nombre, String descripcion, BigDecimal precioBase, String imagenUrl, boolean disponible) {
        this.id = id;
        this.categoria = categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.imagenUrl = imagenUrl;
        this.disponible = disponible;
    }

    public void validarDisponible() {
        if (!this.disponible) {
            throw new ReglaNegocioException("El plato '" + this.nombre + "' no está disponible");
        }
    }

    /**
     * Valida según RN-02 y RN-04, y crea el objeto ModificacionDetalle
     */
    public ModificacionDetalle validarYCrearModificacion(Ingrediente ingrediente, AccionModificacion accion) {
        if (accion == AccionModificacion.QUITAR) {
            // Verificar si está en la receta y si es removible
            PlatoIngrediente itemReceta = receta.stream()
                .filter(pi -> pi.getIngrediente().getId().equals(ingrediente.getId()))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("El ingrediente '" + ingrediente.getNombre() + "' no forma parte de la receta de " + this.nombre));

            if (!itemReceta.isRemovible()) {
                throw new ReglaNegocioException("El ingrediente '" + ingrediente.getNombre() + "' no es removible en " + this.nombre);
            }
            return new ModificacionDetalle(ingrediente, accion, BigDecimal.ZERO);
        } else if (accion == AccionModificacion.AGREGAR) {
            if (!ingrediente.isDisponible()) {
                throw new ReglaNegocioException("El ingrediente extra '" + ingrediente.getNombre() + "' no está disponible");
            }
            // Verificar si es adicionable para este plato
            PlatoIngrediente itemReceta = receta.stream()
                .filter(pi -> pi.getIngrediente().getId().equals(ingrediente.getId()))
                .findFirst()
                .orElse(null);

            if (itemReceta != null && !itemReceta.isAdicionable()) {
                throw new ReglaNegocioException("El ingrediente '" + ingrediente.getNombre() + "' no se puede adicionar a " + this.nombre);
            }
            return new ModificacionDetalle(ingrediente, accion, ingrediente.getPrecioExtra());
        }
        throw new ReglaNegocioException("Acción de modificación no soportada: " + accion);
    }

    public void agregarIngredienteReceta(Ingrediente ingrediente, BigDecimal cantidad, boolean removible, boolean adicionable) {
        PlatoIngrediente pi = new PlatoIngrediente(this, ingrediente, cantidad, removible, adicionable);
        this.receta.add(pi);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public List<PlatoIngrediente> getReceta() { return receta; }
    public void setReceta(List<PlatoIngrediente> receta) { this.receta = receta; }
}
