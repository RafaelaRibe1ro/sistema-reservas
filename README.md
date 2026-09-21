# Sistema de Reservas — TP1 + TP3

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
├── servico-reserva/
└── servico-auth/
```

---

# TP3 — Autenticação e Autorização

Esta seção documenta exclusivamente a entrega do **TP3**: a adição de autenticação e autorização ao sistema de reservas descrito acima. O TP1 (arquitetura de microservices, resiliência, persistência poliglota) não foi alterado — o TP3 apenas acrescenta um novo microservice de autenticação e protege as rotas de `servico-reserva`.

## Arquitetura da solução

- **servico-auth** (novo microservice, independente dos demais): concentra toda a lógica de autenticação — cadastro de credenciais, login e emissão/renovação de tokens JWT. Tem seu próprio banco (`bd_auth`, PostgreSQL) e não faz nenhuma chamada aos outros microservices em tempo de execução.
- **servico-reserva**: recebeu um filtro (`JwtAuthFilter`) que passa a exigir um JWT válido, assinado pelo `servico-auth`, em toda requisição — é o microservice usado para demonstrar rotas protegidas.
- **api-gateway**: ganhou uma nova rota (`/api/auth/**` → `servico-auth`), seguindo o mesmo padrão de roteamento já usado para `/api/usuarios`, `/api/recursos` e `/api/reservas`.
- `servico-usuario` e `servico-recurso` não foram alterados nesta entrega.

O `servico-auth` e o `servico-reserva` compartilham apenas o **segredo** usado para assinar/validar o token (`jwt.secret` no `application.yml` de cada um) — não há acoplamento de código nem chamadas HTTP entre eles, o que preserva a independência exigida para o serviço de autenticação.

## Tecnologia escolhida

**JWT (JSON Web Token)**, via biblioteca `io.jsonwebtoken:jjwt` (0.12.x), assinatura **HS256** com chave simétrica compartilhada. As senhas das credenciais são armazenadas com hash **BCrypt** (`spring-security-crypto`), nunca em texto puro.

- **Access token**: validade de 15 minutos (`900000` ms) — enviado em toda requisição a rotas protegidas.
- **Refresh token**: validade de 24 horas (`86400000` ms) — usado apenas no endpoint de refresh para obter um novo access token, sem exigir login novamente.

## Como executar

Além dos passos já descritos na seção TP1, é necessário subir também o `servico-auth`:

```bash
# 1) Bancos (recria o volume do Postgres se ele já existir sem o bd_auth — ver observação abaixo)
docker compose up -d

# 2) Discovery Server
cd discovery-server && ./mvnw spring-boot:run

# 3) Serviços de negócio (qualquer ordem entre eles)
cd servico-usuario && ./mvnw spring-boot:run
cd servico-recurso && ./mvnw spring-boot:run
cd servico-reserva && ./mvnw spring-boot:run
cd servico-auth && ./mvnw spring-boot:run

# 4) API Gateway
cd api-gateway && ./mvnw spring-boot:run
```

No Windows use `./mvnw.cmd spring-boot:run`.

> **Observação:** o script `docker/inicializacao-postgres/criar-bancos.sh` só roda na primeira criação do volume `dados_postgres`. Se você já tinha subido o `docker compose` do TP1 antes desta entrega, o banco `bd_auth` não existirá ainda. Rode `docker exec -it reservas-postgres psql -U reservas -d postgres -c "CREATE DATABASE bd_auth;"` uma vez, ou remova o volume (`docker compose down -v`) para recriá-lo do zero.

### Portas

| Serviço | Porta |
|---|---|
| servico-auth | 8084 |

(demais portas seguem a tabela da seção TP1)

## Como realizar a autenticação

**1) Cadastrar uma credencial** (necessário antes do primeiro login):

```bash
POST http://localhost:8080/api/auth/registrar
```

**2) Login** — retorna `accessToken` e `refreshToken`:

```bash
POST http://localhost:8080/api/auth/login
```

## Como utilizar o endpoint de refresh

Quando o `accessToken` expirar (15 min), use o `refreshToken` para obter um novo `accessToken` sem repetir o login:

```bash
POST http://localhost:8080/api/auth/refresh
```

A resposta tem o mesmo formato do login, com um novo `accessToken` (e um novo `refreshToken`).

## Endpoints públicos

| Método | Rota (via gateway) | Descrição |
|---|---|---|
| POST | `/api/auth/registrar` | Cadastra uma credencial (email + senha) |
| POST | `/api/auth/login` | Autentica e retorna access/refresh token |
| POST | `/api/auth/refresh` | Renova o access token a partir do refresh token |

Os endpoints de `servico-usuario` e `servico-recurso` permanecem públicos nesta entrega (fora do escopo do TP3).

## Endpoints protegidos

Todas as rotas de `servico-reserva` exigem o header `Authorization: Bearer <accessToken>`:

| Método | Rota (via gateway) |
|---|---|
| POST | `/api/reservas` |
| GET | `/api/reservas` |
| GET | `/api/reservas/{id}` |
| DELETE | `/api/reservas/{id}` |

Requisição sem token, ou com token inválido/expirado, recebe **401 Unauthorized** do próprio `servico-reserva` (o `JwtAuthFilter` intercepta a requisição antes de chegar ao controller).
