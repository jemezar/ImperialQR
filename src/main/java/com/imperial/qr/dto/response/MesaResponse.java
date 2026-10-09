package com.imperial.qr.dto.response;

public record MesaResponse(
    Long id,
    Integer numero,
    Integer capacidad,
    String codigoQr,
    String estado,
    OrdenResponse ordenActiva
) {
    public static MesaResponse sinOrden(Long id, Integer numero, Integer capacidad, String codigoQr, String estado) {
        return new MesaResponse(id, numero, capacidad, codigoQr, estado, null);
    }
}
