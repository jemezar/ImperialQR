package com.imperial.qr.domain.enums;

public enum EstadoDetalle {
    RECIBIDO,
    EN_PREPARACION,
    LISTO,
    ENTREGADO,
    CANCELADO;

    /**
     * Regla de negocio RN-05:
     * El estado de un plato solo avanza en el orden RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO.
     * Unicamente RECIBIDO puede pasar a CANCELADO.
     */
    public boolean puedePasarA(EstadoDetalle destino) {
        if (destino == null) {
            return false;
        }
        return switch (this) {
            case RECIBIDO -> destino == EN_PREPARACION || destino == CANCELADO;
            case EN_PREPARACION -> destino == LISTO;
            case LISTO -> destino == ENTREGADO;
            default -> false;
        };
    }
}
