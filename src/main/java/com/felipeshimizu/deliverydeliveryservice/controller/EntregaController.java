package com.felipeshimizu.deliverydeliveryservice.controller;

import com.felipeshimizu.deliverydeliveryservice.service.EntregaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/entregas")
public class EntregaController {

    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @PostMapping("/{pedidoId}/confirmar")
    public ResponseEntity<Void> confirmarEntrega(@PathVariable UUID pedidoId) {
        entregaService.confirmarEntrega(pedidoId);
        return ResponseEntity.ok().build();
    }
}
