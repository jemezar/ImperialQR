package com.imperial.qr.controller;

import com.imperial.qr.dto.request.CambioEstadoDetalleRequest;
import com.imperial.qr.dto.response.DetalleOrdenResponse;
import com.imperial.qr.security.JwtService;
import com.imperial.qr.service.CocinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cocina")
@Tag(name = "Cocina", description = "Endpoints para el personal de cocina")
public class CocinaController {

    private final CocinaService cocinaService;
    private final JwtService jwtService;

    public CocinaController(CocinaService cocinaService, JwtService jwtService) {
        this.cocinaService = cocinaService;
        this.jwtService = jwtService;
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('COCINERO', 'ADMIN')")
    @Operation(summary = "Comandas pendientes", description = "RF-07: Lista platos en estado RECIBIDO y EN_PREPARACION ordenados por antigüedad")
    public ResponseEntity<List<DetalleOrdenResponse>> listarPendientes() {
        return ResponseEntity.ok(cocinaService.listarPendientes());
    }

    @PatchMapping("/detalles/{id}/estado")
    @PreAuthorize("hasAnyRole('COCINERO', 'ADMIN')")
    @Operation(summary = "Avanzar preparación de plato", description = "RF-07 / RN-06: Cambia estado de plato a EN_PREPARACION o LISTO")
    public ResponseEntity<DetalleOrdenResponse> cambiarEstado(
        @PathVariable Long id,
        @Valid @RequestBody CambioEstadoDetalleRequest request,
        HttpServletRequest httpRequest
    ) {
        Long usuarioId = extraerUsuarioId(httpRequest);
        return ResponseEntity.ok(cocinaService.cambiarEstado(id, request.nuevoEstado(), usuarioId));
    }

    private Long extraerUsuarioId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtService.extraerId(authHeader.substring(7));
        }
        return null;
    }
}
