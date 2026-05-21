package com.felipeshimizu.deliverydeliveryservice.mapper;

import com.felipeshimizu.deliverydeliveryservice.dto.EntregaResponseDTO;
import com.felipeshimizu.deliverydeliveryservice.model.Entrega;

public class EntregaMapper {
    public static EntregaResponseDTO toResponseDTO(Entrega entrega) {
        return new EntregaResponseDTO(
                entrega.getId(),
                entrega.getPedidoId(),
                entrega.getStatus(),
                entrega.getIniciadoEm(),
                entrega.getEntregador() != null ? entrega.getEntregador().getId() : null
        );
    }
}
