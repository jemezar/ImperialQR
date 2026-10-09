package com.imperial.qr.controller;

import com.imperial.qr.dto.request.MesaRequest;
import com.imperial.qr.dto.response.MesaResponse;
import com.imperial.qr.service.MesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mesas")
@Tag(name = "Mesas y Códigos QR", description = "Validación de códigos QR y administración de mesas")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping("/qr/{codigoQr}")
    @Operation(summary = "Validar QR de mesa", description = "Endpoint público para clientes que escanean el QR en mesa; devuelve mesa y orden activa")
    public ResponseEntity<MesaResponse> obtenerPorQr(@PathVariable String codigoQr) {
        return ResponseEntity.ok(mesaService.obtenerPorCodigoQr(codigoQr));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las mesas", description = "Lista todas las mesas del restaurante con su estado y QR")
    public ResponseEntity<List<MesaResponse>> listarTodas() {
        return ResponseEntity.ok(mesaService.listarTodas());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva mesa", description = "Crea una mesa y genera su UUID para QR")
    public ResponseEntity<MesaResponse> crear(@Valid @RequestBody MesaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.crearMesa(request));
    }

    @PutMapping("/{id}/regenerar-qr")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Regenerar código QR", description = "Genera un nuevo UUID para el código QR de la mesa")
    public ResponseEntity<MesaResponse> regenerarQr(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.regenerarQr(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar mesa", description = "Elimina una mesa del sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mesaService.eliminarMesa(id);
        return ResponseEntity.noContent().build();
    }
}
