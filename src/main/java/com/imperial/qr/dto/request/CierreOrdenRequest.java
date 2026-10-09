package com.imperial.qr.dto.request;

public record CierreOrdenRequest(
    Boolean incluirPropina
) {
    public boolean getIncluirPropina() {
        return incluirPropina == null || incluirPropina;
    }
}
