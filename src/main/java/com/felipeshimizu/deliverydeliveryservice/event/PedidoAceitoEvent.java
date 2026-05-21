package com.felipeshimizu.deliverydeliveryservice.event;

import java.util.UUID;

public class PedidoAceitoEvent {
    private UUID pedidoId;
    private UUID restauranteId;

    public PedidoAceitoEvent() {
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public UUID getRestauranteId() {
        return restauranteId;
    }
}
