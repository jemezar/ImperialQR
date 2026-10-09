package com.imperial.qr.controller;

import com.imperial.qr.dto.request.CierreOrdenRequest;
import com.imperial.qr.dto.request.CrearOrdenRequest;
import com.imperial.qr.dto.request.RegistrarEncuestaRequest;
import com.imperial.qr.dto.request.RegistrarPagoRequest;
import com.imperial.qr.dto.response.EncuestaResponse;
import com.imperial.qr.dto.response.OrdenResponse;
import com.imperial.qr.dto.response.PagoResponse;
import com.imperial.qr.service.EncuestaService;
import com.imperial.qr.service.OrdenService;
import com.imperial.qr.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@Tag(name = "Órdenes", description = "Gestión del ciclo de vida de pedidos, adiciones, cierres, pagos y encuestas")
public class OrdenController {

    private final OrdenService ordenService;
    private final PagoService pagoService;
    private final EncuestaService encuestaService;

    public OrdenController(OrdenService ordenService, PagoService pagoService, EncuestaService encuestaService) {
        this.ordenService = ordenService;
        this.pagoService = pagoService;
        this.encuestaService = encuestaService;
    }

    @PostMapping
    @Operation(summary = "Crear o adicionar pedido a mesa", description = "Crea una orden ABIERTA o adiciona platos a la orden activa de la mesa vía QR o mesaId")
    public ResponseEntity<OrdenResponse> crearOAdicionar(@Valid @RequestBody CrearOrdenRequest request) {
        OrdenResponse respuesta;
        if (request.codigoQr() != null && !request.codigoQr().isBlank()) {
            respuesta = ordenService.crear(request.codigoQr(), request);
        } else if (request.mesaId() != null) {
            respuesta = ordenService.crearPorMesaId(request.mesaId(), request);
        } else {
            throw new IllegalArgumentException("Debe proporcionar codigoQr o mesaId");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar orden por ID", description = "Consulta la orden con el detalle y estado de cada plato")
    public ResponseEntity<OrdenResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.obtenerPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dashboard de pedidos", description = "Lista órdenes filtrables por estado, mesa y tipo (MESA o DOMICILIO)")
    public ResponseEntity<List<OrdenResponse>> listarConFiltros(
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) Long mesa,
        @RequestParam(required = false) String tipo
    ) {
        return ResponseEntity.ok(ordenService.listarConFiltros(estado, mesa, tipo));
    }

    @PostMapping("/{id}/cierre")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cerrar cuenta", description = "RF-11 / RN-08: Cierra la orden si todos sus platos fueron entregados o cancelados, calculando impuestos y totales")
    public ResponseEntity<OrdenResponse> cerrarCuenta(@PathVariable Long id, @RequestBody(required = false) CierreOrdenRequest request) {
        boolean incluirPropina = request == null || request.getIncluirPropina();
        return ResponseEntity.ok(ordenService.cerrar(id, incluirPropina));
    }

    @PostMapping("/{id}/pagos")
    @Operation(summary = "Registrar pago", description = "RF-12 / RN-10: Registra pago total o parcial con estrategia polimórfica")
    public ResponseEntity<PagoResponse> registrarPago(@PathVariable Long id, @Valid @RequestBody RegistrarPagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registrarPago(id, request));
    }

    @GetMapping("/{id}/pagos")
    @Operation(summary = "Listar pagos de la orden", description = "Lista el historial de pagos asociados a la orden")
    public ResponseEntity<List<PagoResponse>> listarPagos(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.listarPagosPorOrden(id));
    }

    @PostMapping("/{id}/encuesta")
    @Operation(summary = "Registrar encuesta", description = "RF-13 / RN-11: Registra calificación de satisfacción una vez pagada la orden")
    public ResponseEntity<EncuestaResponse> registrarEncuesta(@PathVariable Long id, @Valid @RequestBody RegistrarEncuestaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(encuestaService.registrarEncuesta(id, request));
    }
}
