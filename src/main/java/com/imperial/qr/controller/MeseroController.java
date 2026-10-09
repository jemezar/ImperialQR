package com.imperial.qr.controller;

import com.imperial.qr.dto.response.DetalleOrdenResponse;
import com.imperial.qr.security.JwtService;
import com.imperial.qr.service.MeseroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mesero")
@Tag(name = "Mesero", description = "Endpoints para el personal de servicio en mesa")
public class MeseroController {

    private final MeseroService meseroService;
    private final JwtService jwtService;

    public MeseroController(MeseroService meseroService, JwtService jwtService) {
        this.meseroService = meseroService;
        this.jwtService = jwtService;
    }

    @GetMapping("/listos")
    @PreAuthorize("hasAnyRole('MESERO', 'ADMIN')")
    @Operation(summary = "Platos listos para entregar", description = "RF-08: Lista platos en estado LISTO indicando la mesa de destino")
    public ResponseEntity<List<DetalleOrdenResponse>> listarListos() {
        return ResponseEntity.ok(meseroService.listarPlatosListos());
    }

    @PatchMapping("/detalles/{id}/entregar")
    @PreAuthorize("hasAnyRole('MESERO', 'ADMIN')")
    @Operation(summary = "Marcar plato como entregado", description = "RF-08 / RN-06: Marca el plato como ENTREGADO en la mesa")
    public ResponseEntity<DetalleOrdenResponse> marcarEntregado(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long usuarioId = extraerUsuarioId(httpRequest);
        return ResponseEntity.ok(meseroService.marcarEntregado(id, usuarioId));
    }

    private Long extraerUsuarioId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtService.extraerId(authHeader.substring(7));
        }
        return null;
    }
}
