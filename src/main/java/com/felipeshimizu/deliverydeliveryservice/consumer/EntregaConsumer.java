package com.felipeshimizu.deliverydeliveryservice.consumer;

import com.felipeshimizu.deliverydeliveryservice.event.PedidoAceitoEvent;
import com.felipeshimizu.deliverydeliveryservice.service.EntregaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class EntregaConsumer {
    private static final Logger log = LoggerFactory.getLogger(EntregaConsumer.class);

    private final EntregaService entregaService;
    private final ObjectMapper objectMapper;

    public EntregaConsumer(EntregaService entregaService, ObjectMapper objectMapper) {
        this.entregaService = entregaService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "pedido-aceito", groupId = "${spring.kafka.consumer.group-id}")
    public void consumir(String mensagem) {
        try {
            PedidoAceitoEvent event = objectMapper.readValue(mensagem, PedidoAceitoEvent.class);
            log.info("Evento recebido para o pedido {}", event.getPedidoId());
            entregaService.processarPedido(event);
        } catch (Exception e) {
            log.error("Erro ao processar evento: {}", e.getMessage());
        }
    }
}
