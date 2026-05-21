package com.felipeshimizu.deliverydeliveryservice.event;

import java.util.UUID;

public class PedidoSaiuEntregaEvent {
    private UUID pedidoId;
    private UUID entregadorId;

    public PedidoSaiuEntregaEvent() {
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public UUID getEntregadorId() {
        return entregadorId;
    }

    public void setPedidoId(UUID pedidoId) {
        this.pedidoId = pedidoId;
    }

    public void setEntregadorId(UUID entregadorId) {
        this.entregadorId = entregadorId;
    }
}
