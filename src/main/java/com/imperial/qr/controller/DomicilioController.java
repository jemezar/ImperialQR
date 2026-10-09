package com.imperial.qr.controller;

import com.imperial.qr.domain.enums.EstadoDomicilio;
import com.imperial.qr.dto.request.ActualizarDomicilioRequest;
import com.imperial.qr.dto.request.CrearDomicilioRequest;
import com.imperial.qr.dto.response.DomicilioResponse;
import com.imperial.qr.security.JwtService;
import com.imperial.qr.service.DomicilioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/domicilios")
@Tag(name = "Domicilios", description = "Gestión de pedidos a domicilio, asignación y seguimiento de entregas")
public class DomicilioController {

    private final DomicilioService domicilioService;
    private final JwtService jwtService;

    public DomicilioController(DomicilioService domicilioService, JwtService jwtService) {
        this.domicilioService = domicilioService;
        this.jwtService = jwtService;
    }

    @PostMapping
    @Operation(summary = "Crear pedido a domicilio", description = "RF-19 / RN-12: Crea un pedido a domicilio con datos del cliente y costo de envío")
    public ResponseEntity<DomicilioResponse> crear(@Valid @RequestBody CrearDomicilioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(domicilioService.crearDomicilio(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar domicilio por ID", description = "Consulta el estado y trazabilidad del pedido a domicilio")
    public ResponseEntity<DomicilioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(domicilioService.obtenerPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOMICILIARIO')")
    @Operation(summary = "Listar domicilios", description = "Lista pedidos a domicilio filtrables opcionalmente por estado")
    public ResponseEntity<List<DomicilioResponse>> listar(@RequestParam(required = false) EstadoDomicilio estado) {
        return ResponseEntity.ok(domicilioService.listar(estado));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOMICILIARIO')")
    @Operation(summary = "Actualizar estado y asignar domiciliario", description = "RF-20: Asigna un repartidor o avanza el estado a EN_CAMINO o ENTREGADO")
    public ResponseEntity<DomicilioResponse> actualizar(
        @PathVariable Long id,
        @RequestBody ActualizarDomicilioRequest request,
        HttpServletRequest httpRequest
    ) {
        Long domiciliarioId = request.domiciliarioId();
        if (domiciliarioId == null) {
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                domiciliarioId = jwtService.extraerId(authHeader.substring(7));
            }
        }
        return ResponseEntity.ok(domicilioService.actualizarDomicilio(id, domiciliarioId, request.estado()));
    }
}
