package com.felipeshimizu.deliverydeliveryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EntregadorRequestDTO(

        @NotBlank(message = "O nome do entregador é obrigatório")
        String nome,

        @NotNull(message = "A disponibilidade do entregador é obrigatória")
        Boolean disponivel
) {
}
