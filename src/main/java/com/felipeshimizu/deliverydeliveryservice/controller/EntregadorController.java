package com.felipeshimizu.deliverydeliveryservice.controller;

import com.felipeshimizu.deliverydeliveryservice.dto.EntregadorRequestDTO;
import com.felipeshimizu.deliverydeliveryservice.dto.EntregadorResponseDTO;
import com.felipeshimizu.deliverydeliveryservice.service.EntregaService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/entregadores")
public class EntregadorController {
    private final EntregaService entregaService;

    public EntregadorController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @PostMapping
    public ResponseEntity<EntregadorResponseDTO> criarEntregador(@RequestBody @Valid EntregadorRequestDTO dto) {
        EntregadorResponseDTO entregador = entregaService.cadastrarEntregador(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(entregador);
    }

    @GetMapping
    public ResponseEntity<Page<EntregadorResponseDTO>> buscarEntregadores(Pageable pageable) {
        return ResponseEntity.ok(entregaService.listarEntregadores(pageable));
    }
}
