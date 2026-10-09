package com.imperial.qr.controller;

import com.imperial.qr.dto.response.CategoriaResponse;
import com.imperial.qr.service.CartaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carta")
@Tag(name = "Carta Pública", description = "Consulta pública del menú digital del restaurante")
public class CartaController {

    private final CartaService cartaService;

    public CartaController(CartaService cartaService) {
        this.cartaService = cartaService;
    }

    @GetMapping
    @Operation(summary = "Consultar carta digital", description = "Retorna categorías con platos disponibles y opciones de receta para personalización")
    public ResponseEntity<List<CategoriaResponse>> obtenerCarta() {
        return ResponseEntity.ok(cartaService.obtenerCartaPublica());
    }
}
