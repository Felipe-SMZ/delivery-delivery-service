package com.felipeshimizu.deliverydeliveryservice.dto;

import java.util.List;
import java.util.UUID;

public record EntregadorResponseDTO(

        UUID id,
        String nome,
        Boolean disponivel,
        List<EntregaResponseDTO> entregas
) {
}
