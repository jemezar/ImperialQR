package com.imperial.qr.service.impl;

import com.imperial.qr.domain.model.Categoria;
import com.imperial.qr.domain.model.Ingrediente;
import com.imperial.qr.domain.model.Plato;
import com.imperial.qr.dto.request.CategoriaRequest;
import com.imperial.qr.dto.request.IngredienteRequest;
import com.imperial.qr.dto.request.PlatoRequest;
import com.imperial.qr.dto.response.CategoriaResponse;
import com.imperial.qr.dto.response.IngredienteResponse;
import com.imperial.qr.dto.response.PlatoResponse;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.CategoriaRepository;
import com.imperial.qr.repository.IngredienteRepository;
import com.imperial.qr.repository.PlatoRepository;
import com.imperial.qr.service.CartaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartaServiceImpl implements CartaService {

    private final CategoriaRepository categoriaRepository;
    private final PlatoRepository platoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final OrdenMapper mapper;

    public CartaServiceImpl(CategoriaRepository categoriaRepository,
                            PlatoRepository platoRepository,
                            IngredienteRepository ingredienteRepository,
                            OrdenMapper mapper) {
        this.categoriaRepository = categoriaRepository;
        this.platoRepository = platoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> obtenerCartaPublica() {
        return categoriaRepository.findByActivoTrue().stream()
            .map(mapper::toCategoriaResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategorias() {
        return categoriaRepository.findAll().stream()
            .map(mapper::toCategoriaResponse)
            .toList();
    }

    @Override
    @Transactional
    public CategoriaResponse crearCategoria(CategoriaRequest request) {
        Categoria cat = new Categoria(request.nombre(), request.descripcion());
        if (request.activo() != null) cat.setActivo(request.activo());
        return mapper.toCategoriaResponse(categoriaRepository.save(cat));
    }

    @Override
    @Transactional
    public CategoriaResponse actualizarCategoria(Long id, CategoriaRequest request) {
        Categoria cat = categoriaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));
        cat.setNombre(request.nombre());
        cat.setDescripcion(request.descripcion());
        if (request.activo() != null) cat.setActivo(request.activo());
        return mapper.toCategoriaResponse(categoriaRepository.save(cat));
    }

    @Override
    @Transactional
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatoResponse> listarPlatos() {
        return platoRepository.findAll().stream()
            .map(mapper::toPlatoResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlatoResponse obtenerPlatoPorId(Long id) {
        Plato plato = platoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado con ID: " + id));
        return mapper.toPlatoResponse(plato);
    }

    @Override
    @Transactional
    public PlatoResponse crearPlato(PlatoRequest request) {
        Categoria cat = categoriaRepository.findById(request.categoriaId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + request.categoriaId()));

        Plato plato = new Plato(
            null,
            cat,
            request.nombre(),
            request.descripcion(),
            request.precioBase(),
            request.imagenUrl(),
            request.disponible() == null || request.disponible()
        );

        if (request.receta() != null) {
            for (var item : request.receta()) {
                Ingrediente ing = ingredienteRepository.findById(item.ingredienteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Ingrediente no encontrado: " + item.ingredienteId()));
                BigDecimal cantidad = item.cantidad() != null ? item.cantidad() : BigDecimal.ONE;
                boolean removible = item.removible() == null || item.removible();
                boolean adicionable = item.adicionable() == null || item.adicionable();
                plato.agregarIngredienteReceta(ing, cantidad, removible, adicionable);
            }
        }

        return mapper.toPlatoResponse(platoRepository.save(plato));
    }

    @Override
    @Transactional
    public PlatoResponse actualizarPlato(Long id, PlatoRequest request) {
        Plato plato = platoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado con ID: " + id));

        Categoria cat = categoriaRepository.findById(request.categoriaId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + request.categoriaId()));

        plato.setCategoria(cat);
        plato.setNombre(request.nombre());
        plato.setDescripcion(request.descripcion());
        plato.setPrecioBase(request.precioBase());
        plato.setImagenUrl(request.imagenUrl());
        if (request.disponible() != null) plato.setDisponible(request.disponible());

        if (request.receta() != null) {
            plato.getReceta().clear();
            for (var item : request.receta()) {
                Ingrediente ing = ingredienteRepository.findById(item.ingredienteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Ingrediente no encontrado: " + item.ingredienteId()));
                BigDecimal cantidad = item.cantidad() != null ? item.cantidad() : BigDecimal.ONE;
                boolean removible = item.removible() == null || item.removible();
                boolean adicionable = item.adicionable() == null || item.adicionable();
                plato.agregarIngredienteReceta(ing, cantidad, removible, adicionable);
            }
        }

        return mapper.toPlatoResponse(platoRepository.save(plato));
    }

    @Override
    @Transactional
    public void cambiarDisponibilidadPlato(Long id, boolean disponible) {
        Plato plato = platoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado con ID: " + id));
        plato.setDisponible(disponible);
        platoRepository.save(plato);
    }

    @Override
    @Transactional
    public void eliminarPlato(Long id) {
        if (!platoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Plato no encontrado con ID: " + id);
        }
        platoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredienteResponse> listarIngredientes() {
        return ingredienteRepository.findAll().stream()
            .map(mapper::toIngredienteResponse)
            .toList();
    }

    @Override
    @Transactional
    public IngredienteResponse crearIngrediente(IngredienteRequest request) {
        Ingrediente ing = new Ingrediente(
            request.nombre(),
            request.unidad(),
            request.precioExtra(),
            request.disponible() == null || request.disponible()
        );
        return mapper.toIngredienteResponse(ingredienteRepository.save(ing));
    }

    @Override
    @Transactional
    public IngredienteResponse actualizarIngrediente(Long id, IngredienteRequest request) {
        Ingrediente ing = ingredienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Ingrediente no encontrado con ID: " + id));
        ing.setNombre(request.nombre());
        if (request.unidad() != null) ing.setUnidad(request.unidad());
        ing.setPrecioExtra(request.precioExtra());
        if (request.disponible() != null) ing.setDisponible(request.disponible());
        return mapper.toIngredienteResponse(ingredienteRepository.save(ing));
    }

    @Override
    @Transactional
    public void cambiarDisponibilidadIngrediente(Long id, boolean disponible) {
        Ingrediente ing = ingredienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Ingrediente no encontrado con ID: " + id));
        ing.setDisponible(disponible);
        ingredienteRepository.save(ing);
    }

    @Override
    @Transactional
    public void eliminarIngrediente(Long id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Ingrediente no encontrado con ID: " + id);
        }
        ingredienteRepository.deleteById(id);
    }
}
