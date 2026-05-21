package com.felipeshimizu.deliverydeliveryservice.service;

import com.felipeshimizu.deliverydeliveryservice.dto.EntregadorRequestDTO;
import com.felipeshimizu.deliverydeliveryservice.dto.EntregadorResponseDTO;
import com.felipeshimizu.deliverydeliveryservice.event.PedidoAceitoEvent;
import com.felipeshimizu.deliverydeliveryservice.event.PedidoEntregueEvent;
import com.felipeshimizu.deliverydeliveryservice.event.PedidoSaiuEntregaEvent;
import com.felipeshimizu.deliverydeliveryservice.exception.EntregaNotFoundException;
import com.felipeshimizu.deliverydeliveryservice.mapper.EntregadorMapper;
import com.felipeshimizu.deliverydeliveryservice.model.Entrega;
import com.felipeshimizu.deliverydeliveryservice.model.Entregador;
import com.felipeshimizu.deliverydeliveryservice.model.enums.StatusEntrega;
import com.felipeshimizu.deliverydeliveryservice.producer.EntregaProducer;
import com.felipeshimizu.deliverydeliveryservice.repository.EntregaRepository;
import com.felipeshimizu.deliverydeliveryservice.repository.EntregadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EntregaService {
    private static final Logger log = LoggerFactory.getLogger(EntregaService.class);

    private final EntregadorRepository entregadorRepository;
    private final EntregaRepository entregaRepository;
    private final EntregaProducer entregaProducer;

    public EntregaService(EntregadorRepository entregadorRepository,
                          EntregaRepository entregaRepository,
                          EntregaProducer entregaProducer) {
        this.entregadorRepository = entregadorRepository;
        this.entregaRepository = entregaRepository;
        this.entregaProducer = entregaProducer;
    }

    public EntregadorResponseDTO cadastrarEntregador(EntregadorRequestDTO dto) {
        Entregador entregador = EntregadorMapper.toEntity(dto);
        Entregador salvo = entregadorRepository.save(entregador);
        log.info("Entregador cadastrado com ID {}", salvo.getId());
        return EntregadorMapper.toResponseDTO(salvo);
    }

    public Page<EntregadorResponseDTO> listarEntregadores(Pageable pageable) {
        return entregadorRepository.findAll(pageable)
                .map(EntregadorMapper::toResponseDTO);
    }

    @Transactional
    public void processarPedido(PedidoAceitoEvent event) {
        // 1. Verifica idempotência
        if (entregaRepository.existsByPedidoId(event.getPedidoId())) {
            log.warn("Pedido {} já processado, ignorando", event.getPedidoId());
            return;
        }

        // 2. Busca entregador disponível
        Entregador entregador = entregadorRepository.findFirstByDisponivelTrue()
                .orElseThrow(() -> new RuntimeException("Nenhum entregador disponível"));

        // 3. Marca entregador como indisponível
        entregador.setDisponivel(false);
        entregadorRepository.save(entregador);

        // 4. Cria a entrega
        Entrega entrega = new Entrega();
        entrega.setPedidoId(event.getPedidoId());
        entrega.setStatus(StatusEntrega.SAIU_PARA_ENTREGA);
        entrega.setEntregador(entregador);
        entregaRepository.save(entrega);
        log.info("Pedido {} atribuído ao entregador {}", event.getPedidoId(), entregador.getId());

        // 5. Publica evento pedido-saiu-entrega
        PedidoSaiuEntregaEvent pedidoSaiuEntregaEvent = new PedidoSaiuEntregaEvent();
        pedidoSaiuEntregaEvent.setPedidoId(event.getPedidoId());
        pedidoSaiuEntregaEvent.setEntregadorId(entregador.getId());
        entregaProducer.publicar(pedidoSaiuEntregaEvent);
    }

    @Transactional
    public void confirmarEntrega(UUID pedidoId) {
        Entrega entrega = entregaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new EntregaNotFoundException("Entrega não encontrada para o pedido: " + pedidoId));

        // 1. Atualiza status da entrega
        entrega.setStatus(StatusEntrega.ENTREGUE);
        entregaRepository.save(entrega);

        // 2. Libera o entregador
        Entregador entregador = entrega.getEntregador();
        entregador.setDisponivel(true);
        entregadorRepository.save(entregador);
        log.info("Pedido {} entregue pelo entregador {}", pedidoId, entregador.getId());

        // 3. Publica evento pedido-entregue
        PedidoEntregueEvent pedidoEntregueEvent = new PedidoEntregueEvent();
        pedidoEntregueEvent.setPedidoId(pedidoId);
        pedidoEntregueEvent.setEntregadorId(entregador.getId());
        entregaProducer.publicarEntregue(pedidoEntregueEvent);
    }
}


