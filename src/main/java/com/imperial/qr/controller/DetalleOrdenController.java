package com.imperial.qr.controller;

import com.imperial.qr.domain.model.DetalleOrden;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.repository.DetalleOrdenRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/detalles")
@Tag(name = "Detalles de Orden", description = "Operaciones de administración y cancelación sobre items de órdenes")
public class DetalleOrdenController {

    private final DetalleOrdenRepository detalleOrdenRepository;

    public DetalleOrdenController(DetalleOrdenRepository detalleOrdenRepository) {
        this.detalleOrdenRepository = detalleOrdenRepository;
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancelar plato en estado RECIBIDO", description = "RF-21: El administrador puede cancelar un plato si aún se encuentra en estado RECIBIDO")
    public ResponseEntity<Void> cancelarPlato(@PathVariable Long id) {
        DetalleOrden d = detalleOrdenRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Detalle de orden no encontrado con ID: " + id));

        d.cancelar();
        detalleOrdenRepository.save(d);
        return ResponseEntity.ok().build();
    }
}
