# delivery-delivery-service

Microsserviço responsável por gerenciar entregadores e processar entregas. Atua como **Consumer Kafka** do tópico `pedido-aceito` e **Producer Kafka** publicando eventos nos tópicos `pedido-saiu-entrega` e `pedido-entregue`.

---

## Sobre o serviço

Este serviço faz parte de um sistema de delivery construído com arquitetura orientada a eventos. Quando um pedido é aceito pelo restaurante, este serviço consome o evento, atribui um entregador disponível e notifica os demais serviços.

```
restaurant-service → tópico: pedido-aceito → delivery-service → banco de dados
                                                               → tópico: pedido-saiu-entrega → (notification-service)
                                                               → tópico: pedido-entregue     → (notification-service)
```

---

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Data JPA
- Spring for Apache Kafka
- MySQL
- Maven

---

## Pré-requisitos

- Java 21+
- Maven
- MySQL rodando na porta `3307`
- Apache Kafka rodando na porta `9092`

> Para subir o Kafka e o MySQL localmente, use o `docker-compose.yml` disponível no repositório [delivery-infra](https://github.com/Felipe-SMZ/delivery-infra).

---

## Configuração

Copie o arquivo de exemplo e preencha com suas credenciais:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

---

## Como rodar

```bash
# Clone o repositório
git clone https://github.com/Felipe-SMZ/delivery-delivery-service

# Entre na pasta
cd delivery-delivery-service

# Compile e rode
./mvnw spring-boot:run
```

O serviço sobe na porta `8082`.

---

## Endpoints

### Cadastrar entregador
```
POST /entregadores
```

**Body:**
```json
{
  "nome": "João Silva",
  "disponivel": true
}
```

**Resposta `201 Created`:**
```json
{
  "id": "a752276d-e503-45e7-9850-cb01429258c0",
  "nome": "João Silva",
  "disponivel": true,
  "entregas": []
}
```

---

### Listar entregadores
```
GET /entregadores?page=0&size=10&sort=nome,asc
```

**Resposta `200 OK`:**
```json
{
  "content": [...],
  "totalElements": 1,
  "totalPages": 1
}
```

---

### Confirmar entrega
```
POST /entregas/{pedidoId}/confirmar
```

**Resposta `200 OK`**

> Simula a confirmação da entrega pelo entregador. Em um sistema real, essa ação seria disparada pelo aplicativo do entregador ao chegar no destino.

---

## Tópicos Kafka

| Ação | Tópico | Tipo |
|------|--------|------|
| Pedido aceito | `pedido-aceito` | Consumer |
| Pedido saiu para entrega | `pedido-saiu-entrega` | Producer |
| Pedido entregue | `pedido-entregue` | Producer |

---

## Fluxo do Consumer

Quando um evento chega no tópico `pedido-aceito`:

1. Verifica idempotência — se o `pedidoId` já foi processado, ignora
2. Busca o primeiro entregador disponível
3. Marca o entregador como indisponível
4. Salva a entrega com status `SAIU_PARA_ENTREGA`
5. Publica evento no tópico `pedido-saiu-entrega`

Quando o endpoint de confirmação é chamado:

1. Busca a entrega pelo `pedidoId`
2. Atualiza o status para `ENTREGUE`
3. Libera o entregador (disponivel = true)
4. Publica evento no tópico `pedido-entregue`

---

## Estrutura do projeto

```
src/main/java/com/felipeshimizu/deliverydeliveryservice
├── controller       # Endpoints REST
├── service          # Regras de negócio
├── repository       # Acesso ao banco
├── model            # Entidades JPA
│   └── enums        # StatusEntrega
├── dto              # Objetos de entrada e saída da API
├── mapper           # Conversão entidade ↔ DTO
├── event            # Eventos consumidos e publicados no Kafka
├── consumer         # Consumo de eventos do Kafka
├── producer         # Publicação de eventos no Kafka
├── config           # Configuração do Kafka
└── exception        # Exceções e handler global
```

---

## Decisões de arquitetura

**Por que o entregador é marcado como indisponível durante a entrega?**
Para evitar que o mesmo entregador seja atribuído a dois pedidos ao mesmo tempo. Quando a entrega é confirmada, o entregador volta a ficar disponível automaticamente.

**Por que idempotência no Consumer?**
O Kafka pode entregar o mesmo evento mais de uma vez em casos de falha de rede ou reinicialização do serviço. Verificar se o `pedidoId` já existe no banco garante que o pedido não seja atribuído a dois entregadores diferentes.

**Por que a confirmação é um endpoint REST e não um evento Kafka?**
Em um sistema real, a confirmação viria do aplicativo do entregador via REST. Optamos por manter essa abordagem para simular o comportamento real sem precisar de um app mobile.

**Por que banco próprio?**
Cada microsserviço tem seu próprio banco de dados, sem compartilhamento de tabelas. O `pedidoId` armazenado em `entregas` é apenas uma referência ao `order-service`, sem FK real entre bancos.

---

## Outros serviços

| Serviço | Descrição |
|---------|-----------|
| [delivery-order-service](https://github.com/Felipe-SMZ/delivery-order-service) | Recebe e gerencia pedidos |
| [delivery-restaurant-service](https://github.com/Felipe-SMZ/delivery-restaurant-service) | Processa e aceita pedidos |
| [delivery-notification-service](https://github.com/Felipe-SMZ/delivery-notification-service) | Notifica clientes |
| [delivery-infra](https://github.com/Felipe-SMZ/delivery-infra) | Docker Compose com Kafka e MySQL |