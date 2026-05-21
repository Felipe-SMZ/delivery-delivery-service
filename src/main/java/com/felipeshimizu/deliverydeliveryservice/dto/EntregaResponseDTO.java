package com.felipeshimizu.deliverydeliveryservice.dto;

import com.felipeshimizu.deliverydeliveryservice.model.enums.StatusEntrega;

import java.time.LocalDateTime;
import java.util.UUID;

public record EntregaResponseDTO(

        UUID id,
        UUID pedidoId,
        StatusEntrega status,
        LocalDateTime iniciadoEm,
        UUID entregadorId
) {
}
