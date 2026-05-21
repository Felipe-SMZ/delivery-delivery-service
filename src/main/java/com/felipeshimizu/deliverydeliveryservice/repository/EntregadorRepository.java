package com.felipeshimizu.deliverydeliveryservice.repository;

import com.felipeshimizu.deliverydeliveryservice.model.Entregador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EntregadorRepository extends JpaRepository<Entregador, UUID> {
    Optional<Entregador> findFirstByDisponivelTrue();
}
