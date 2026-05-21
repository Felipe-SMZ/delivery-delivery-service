package com.felipeshimizu.deliverydeliveryservice.producer;

import com.felipeshimizu.deliverydeliveryservice.event.PedidoEntregueEvent;
import com.felipeshimizu.deliverydeliveryservice.event.PedidoSaiuEntregaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;



@Service
public class EntregaProducer {
    private static final Logger log = LoggerFactory.getLogger(EntregaProducer.class);
    private static final String TOPICO = "pedido-saiu-entrega";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EntregaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publicar(PedidoSaiuEntregaEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPICO, event.getPedidoId().toString(), eventJson);
            log.info("Evento publicado no tópico {} para o pedido {}", TOPICO, event.getPedidoId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento para o pedido {}", event.getPedidoId(), e);
            throw new RuntimeException("Erro ao publicar evento no Kafka", e);
        }
    }

    public void publicarEntregue(PedidoEntregueEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("pedido-entregue", event.getPedidoId().toString(), eventJson);
            log.info("Evento de entrega concluída publicado para o pedido {}", event.getPedidoId());
        } catch (Exception e) {
            log.error("Erro ao publicar evento de entrega concluída para o pedido {}", event.getPedidoId(), e);
            throw new RuntimeException("Erro ao publicar evento no Kafka", e);
        }
    }
}
