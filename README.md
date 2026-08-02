# Sistema de Reservas — TP1

Rafaela Oliveira Ribeiro
Turma **GRLENGR2C2-N2-L1**

## Descrição do Projeto

Sistema de reservas de recursos compartilhados (salas, quadras, mesas, quartos). Um usuário cadastrado pode consultar o catálogo de recursos disponíveis e criar uma reserva para um período, o sistema valida automaticamente que o usuário e o recurso existem e que não há conflito de horário com outra reserva confirmada para o mesmo recurso.

## Arquitetura

- **discovery-server** (Eureka): permite que os microservices se registrem e se descubram dinamicamente entre si.
- **api-gateway** (Spring Cloud Gateway): ponto único de entrada externo, roteia `/api/**` para o microservice correto via nome lógico registrado no Eureka.
- **servico-usuario**, **servico-recurso**, **servico-reserva**: microservices de negócio, cada um dono do seu próprio banco lógico.
- **Persistência poliglota**: `servico-usuario` e `servico-reserva` usam PostgreSQL, `servico-recurso` usa **MongoDB**, porque cada tipo de recurso (sala/quadra/mesa/quarto) tem atributos diferentes — um schema de documento flexível evita tabelas ou migrations por tipo de recurso (detalhes na seção 4 de `docs/proposta.md`).
- **Resiliência**: `servico-reserva → servico-recurso` usa Timeout + Circuit Breaker + Fallback, `servico-reserva → servico-usuario` usa Timeout + Retry (detalhes na seção 7 de `docs/proposta.md`).

## Microservices

| Serviço | Responsabilidade | Porta | Banco |
|---|---|---|---|
| discovery-server | Registro/descoberta de serviços (Eureka) | 8761 | — |
| api-gateway | Ponto único de entrada / roteamento | 8080 | — |
| servico-usuario | Cadastro de usuários | 8081 | PostgreSQL (`bd_usuario`) |
| servico-recurso | Catálogo de recursos reserváveis | 8082 | MongoDB (`bd_recurso`) |
| servico-reserva | Criação/consulta/cancelamento de reservas | 8083 | PostgreSQL (`bd_reserva`) |

## Tecnologias

Java 21 · Spring Boot 4.1.0 · Spring Cloud 2025.1.2 ("Oakwood") · Spring Cloud Netflix Eureka · Spring Cloud Gateway Server WebMVC · Spring Data JPA (PostgreSQL) · Spring Data MongoDB · Resilience4j · Docker Compose

## Como executar

Pré-requisitos: **Java 21**, **Docker Desktop**.

### 1. Subir os bancos de dados

Na raiz do repositório:

```bash
docker compose up -d
```

### 2. Subir os serviços (em terminais separados, nessa ordem)

```bash
# 1) Discovery Server
cd discovery-server && ./mvnw spring-boot:run

# 2) Serviços de negócio
cd servico-usuario && ./mvnw spring-boot:run
cd servico-recurso && ./mvnw spring-boot:run
cd servico-reserva && ./mvnw spring-boot:run

# 3) API Gateway
cd api-gateway && ./mvnw spring-boot:run
```

No Windows use `./mvnw.cmd spring-boot:run`

### Portas utilizadas

| Serviço | Porta |
|---|---|
| discovery-server | 8761 |
| api-gateway | 8080 |
| servico-usuario | 8081 |
| servico-recurso | 8082 |
| servico-reserva | 8083 |
| PostgreSQL (docker) | 5433 |
| MongoDB (docker) | 27018 |

## Discovery Server

Dashboard: **http://localhost:8761**  mostra os serviços registrados (`API-GATEWAY`, `SERVICO-USUARIO`, `SERVICO-RECURSO`, `SERVICO-RESERVA`) assim que sobem.

## API Gateway

Todas as chamadas externas passam por **http://localhost:8080**:

| Rota externa | Microservice destino |
|---|---|
| `/api/usuarios` | servico-usuario |
| `/api/recursos` | servico-recurso |
| `/api/reservas` | servico-reserva |

## Exemplos de requisições (via API Gateway)

```bash
# Criar usuário
POST http://localhost:8080/api/usuarios

# Listar usuários
GET http://localhost:8080/api/usuarios

# Criar recurso (quadra) - atributos flexíveis, próprios do tipo QUADRA
POST http://localhost:8080/api/recursos

# Criar reserva (substitua os ids pelos retornados nos passos acima)
POST http://localhost:8080/api/reservas

# Consultar reservas de um usuário
GET http://localhost:8080/api/reservas?usuarioId=1

# Cancelar uma reserva
DELETE http://localhost:8080/api/reservas/1
```

## Estrutura do repositório

```
tp1/
├── docker-compose.yml
├── docker/inicializacao-postgres/
├── discovery-server/
├── api-gateway/
├── servico-usuario/
├── servico-recurso/
└── servico-reserva/
```
