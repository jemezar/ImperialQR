package com.imperial.qr.service;

import com.imperial.qr.dto.request.CategoriaRequest;
import com.imperial.qr.dto.request.IngredienteRequest;
import com.imperial.qr.dto.request.PlatoRequest;
import com.imperial.qr.dto.response.CategoriaResponse;
import com.imperial.qr.dto.response.IngredienteResponse;
import com.imperial.qr.dto.response.PlatoResponse;

import java.util.List;

public interface CartaService {
    List<CategoriaResponse> obtenerCartaPublica();

    // Categorías CRUD
    List<CategoriaResponse> listarCategorias();
    CategoriaResponse crearCategoria(CategoriaRequest request);
    CategoriaResponse actualizarCategoria(Long id, CategoriaRequest request);
    void eliminarCategoria(Long id);

    // Platos CRUD
    List<PlatoResponse> listarPlatos();
    PlatoResponse obtenerPlatoPorId(Long id);
    PlatoResponse crearPlato(PlatoRequest request);
    PlatoResponse actualizarPlato(Long id, PlatoRequest request);
    void cambiarDisponibilidadPlato(Long id, boolean disponible);
    void eliminarPlato(Long id);

    // Ingredientes CRUD
    List<IngredienteResponse> listarIngredientes();
    IngredienteResponse crearIngrediente(IngredienteRequest request);
    IngredienteResponse actualizarIngrediente(Long id, IngredienteRequest request);
    void cambiarDisponibilidadIngrediente(Long id, boolean disponible);
    void eliminarIngrediente(Long id);
}
