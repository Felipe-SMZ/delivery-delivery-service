package com.felipeshimizu.deliverydeliveryservice.mapper;

import com.felipeshimizu.deliverydeliveryservice.dto.EntregadorRequestDTO;
import com.felipeshimizu.deliverydeliveryservice.dto.EntregadorResponseDTO;
import com.felipeshimizu.deliverydeliveryservice.model.Entregador;

public class EntregadorMapper {
    public static EntregadorResponseDTO toResponseDTO(Entregador entregador) {
        return new EntregadorResponseDTO(
                entregador.getId(),
                entregador.getNome(),
                entregador.getDisponivel(),
                entregador.getEntregas().stream().map(EntregaMapper::toResponseDTO).toList()
        );
    }

    public static Entregador toEntity(EntregadorRequestDTO dto) {
        Entregador entregador = new Entregador();
        entregador.setNome(dto.nome());
        entregador.setDisponivel(dto.disponivel());
        return entregador;
    }
}
