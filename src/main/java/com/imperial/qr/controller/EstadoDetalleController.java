package com.imperial.qr.controller;

import com.imperial.qr.domain.enums.EstadoDetalle;
import com.imperial.qr.domain.enums.Rol;
import com.imperial.qr.domain.model.Usuario;
import com.imperial.qr.dto.request.CambioEstadoDetalleRequest;
import com.imperial.qr.dto.response.DetalleOrdenResponse;
import com.imperial.qr.exception.AccesoDenegadoException;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.repository.UsuarioRepository;
import com.imperial.qr.security.JwtService;
import com.imperial.qr.service.CocinaService;
import com.imperial.qr.service.MeseroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/detalles")
@Tag(name = "Cambio de Estado de Plato", description = "Endpoint unificado para transiciones de estado de plato según el rol")
public class EstadoDetalleController {

    private final CocinaService cocinaService;
    private final MeseroService meseroService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public EstadoDetalleController(CocinaService cocinaService,
                                   MeseroService meseroService,
                                   JwtService jwtService,
                                   UsuarioRepository usuarioRepository) {
        this.cocinaService = cocinaService;
        this.meseroService = meseroService;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('COCINERO', 'MESERO', 'DOMICILIARIO', 'ADMIN')")
    @Operation(summary = "Cambiar estado de plato por rol", description = "RN-05/RN-06: Permite a Cocinero (EN_PREPARACION, LISTO) o Mesero (ENTREGADO) transicionar el plato")
    public ResponseEntity<DetalleOrdenResponse> cambiarEstado(
        @PathVariable Long id,
        @Valid @RequestBody CambioEstadoDetalleRequest request,
        HttpServletRequest httpRequest
    ) {
        String authHeader = httpRequest.getHeader("Authorization");
        Long usuarioId = null;
        String rolStr = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            usuarioId = jwtService.extraerId(token);
            rolStr = jwtService.extraerRol(token);
        }

        EstadoDetalle destino = request.nuevoEstado();

        // RN-06: Verificación de rol y acción
        if (destino == EstadoDetalle.EN_PREPARACION || destino == EstadoDetalle.LISTO) {
            if ("MESERO".equals(rolStr)) {
                throw new AccesoDenegadoException("Un mesero no puede pasar un plato a " + destino + " (RN-06)");
            }
            return ResponseEntity.ok(cocinaService.cambiarEstado(id, destino, usuarioId));
        } else if (destino == EstadoDetalle.ENTREGADO) {
            if ("COCINERO".equals(rolStr)) {
                throw new AccesoDenegadoException("Un cocinero no puede marcar un plato como ENTREGADO (RN-06)");
            }
            return ResponseEntity.ok(meseroService.marcarEntregado(id, usuarioId));
        }

        throw new IllegalArgumentException("Transición directa no permitida vía este endpoint para estado: " + destino);
    }
}
