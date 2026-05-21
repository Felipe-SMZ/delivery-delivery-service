package com.felipeshimizu.deliverydeliveryservice.repository;

import com.felipeshimizu.deliverydeliveryservice.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EntregaRepository extends JpaRepository<Entrega, UUID> {
    boolean existsByPedidoId(UUID pedidoId);

    Optional<Entrega> findByPedidoId(UUID pedidoId);
}
