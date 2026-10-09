package com.imperial.qr.controller;

import com.imperial.qr.dto.request.CategoriaRequest;
import com.imperial.qr.dto.request.IngredienteRequest;
import com.imperial.qr.dto.request.PlatoRequest;
import com.imperial.qr.dto.response.CategoriaResponse;
import com.imperial.qr.dto.response.IngredienteResponse;
import com.imperial.qr.dto.response.PlatoResponse;
import com.imperial.qr.service.CartaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administración de Carta", description = "CRUD de categorías, platos, ingredientes y recetas")
public class AdministracionCartaController {

    private final CartaService cartaService;

    public AdministracionCartaController(CartaService cartaService) {
        this.cartaService = cartaService;
    }

    // --- CATEGORIAS ---
    @GetMapping("/categorias")
    @Operation(summary = "Listar todas las categorías")
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        return ResponseEntity.ok(cartaService.listarCategorias());
    }

    @PostMapping("/categorias")
    @Operation(summary = "Crear categoría")
    public ResponseEntity<CategoriaResponse> crearCategoria(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartaService.crearCategoria(request));
    }

    @PutMapping("/categorias/{id}")
    @Operation(summary = "Actualizar categoría")
    public ResponseEntity<CategoriaResponse> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(cartaService.actualizarCategoria(id, request));
    }

    @DeleteMapping("/categorias/{id}")
    @Operation(summary = "Eliminar categoría")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        cartaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    // --- PLATOS ---
    @GetMapping("/platos")
    @Operation(summary = "Listar todos los platos")
    public ResponseEntity<List<PlatoResponse>> listarPlatos() {
        return ResponseEntity.ok(cartaService.listarPlatos());
    }

    @GetMapping("/platos/{id}")
    @Operation(summary = "Obtener plato por ID")
    public ResponseEntity<PlatoResponse> obtenerPlato(@PathVariable Long id) {
        return ResponseEntity.ok(cartaService.obtenerPlatoPorId(id));
    }

    @PostMapping("/platos")
    @Operation(summary = "Crear plato con receta")
    public ResponseEntity<PlatoResponse> crearPlato(@Valid @RequestBody PlatoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartaService.crearPlato(request));
    }

    @PutMapping("/platos/{id}")
    @Operation(summary = "Actualizar plato")
    public ResponseEntity<PlatoResponse> actualizarPlato(@PathVariable Long id, @Valid @RequestBody PlatoRequest request) {
        return ResponseEntity.ok(cartaService.actualizarPlato(id, request));
    }

    @PatchMapping("/platos/{id}/disponibilidad")
    @Operation(summary = "Cambiar disponibilidad de plato (RF-22)")
    public ResponseEntity<Void> cambiarDisponibilidadPlato(@PathVariable Long id, @RequestParam boolean disponible) {
        cartaService.cambiarDisponibilidadPlato(id, disponible);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/platos/{id}")
    @Operation(summary = "Eliminar plato")
    public ResponseEntity<Void> eliminarPlato(@PathVariable Long id) {
        cartaService.eliminarPlato(id);
        return ResponseEntity.noContent().build();
    }

    // --- INGREDIENTES ---
    @GetMapping("/ingredientes")
    @Operation(summary = "Listar todos los ingredientes")
    public ResponseEntity<List<IngredienteResponse>> listarIngredientes() {
        return ResponseEntity.ok(cartaService.listarIngredientes());
    }

    @PostMapping("/ingredientes")
    @Operation(summary = "Crear ingrediente")
    public ResponseEntity<IngredienteResponse> crearIngrediente(@Valid @RequestBody IngredienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartaService.crearIngrediente(request));
    }

    @PutMapping("/ingredientes/{id}")
    @Operation(summary = "Actualizar ingrediente")
    public ResponseEntity<IngredienteResponse> actualizarIngrediente(@PathVariable Long id, @Valid @RequestBody IngredienteRequest request) {
        return ResponseEntity.ok(cartaService.actualizarIngrediente(id, request));
    }

    @PatchMapping("/ingredientes/{id}/disponibilidad")
    @Operation(summary = "Cambiar disponibilidad de ingrediente (RF-22)")
    public ResponseEntity<Void> cambiarDisponibilidadIngrediente(@PathVariable Long id, @RequestParam boolean disponible) {
        cartaService.cambiarDisponibilidadIngrediente(id, disponible);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/ingredientes/{id}")
    @Operation(summary = "Eliminar ingrediente")
    public ResponseEntity<Void> eliminarIngrediente(@PathVariable Long id) {
        cartaService.eliminarIngrediente(id);
        return ResponseEntity.noContent().build();
    }
}
