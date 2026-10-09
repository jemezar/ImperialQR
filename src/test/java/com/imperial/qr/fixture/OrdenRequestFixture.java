package com.imperial.qr.fixture;

import com.imperial.qr.domain.enums.AccionModificacion;
import com.imperial.qr.dto.request.CrearOrdenRequest;
import com.imperial.qr.dto.request.ItemOrdenRequest;
import com.imperial.qr.dto.request.ModificacionItemRequest;

import java.util.List;

public class OrdenRequestFixture {

    public static CrearOrdenRequest unPlatoConModificacion(Long platoId, Long ingredienteId, AccionModificacion accion) {
        ModificacionItemRequest mod = new ModificacionItemRequest(ingredienteId, accion);
        ItemOrdenRequest item = new ItemOrdenRequest(platoId, 1, "Observación de prueba", List.of(mod));
        return new CrearOrdenRequest(null, null, List.of(item));
    }

    public static CrearOrdenRequest platoSimple(Long platoId) {
        ItemOrdenRequest item = new ItemOrdenRequest(platoId, 1, null, List.of());
        return new CrearOrdenRequest(null, null, List.of(item));
    }
}
