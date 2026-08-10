# SRM Credit Engine

API REST para cálculo de valor presente e liquidação de recebíveis, com suporte a múltiplas moedas, taxa de câmbio e consulta paginada de liquidações.

## Tecnologias

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL 16
- Flyway
- Maven
- JUnit 5
- Mockito
- H2 para testes
- SpringDoc OpenAPI / Swagger
- Docker / Docker Compose
- Lombok

## Arquitetura

O projeto segue uma arquitetura em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Estrutura principal:

```text
src/main/java/com/srm/casedev
├── api
│   └── dto
│       ├── exchange
│       │   ├── ExchangeRateRequest
│       │   └── ExchangeRateResponse
│       └── settlement
│           ├── SettlementReportProjection
│           ├── SettlementRequest
│           └── SettlementResponse
├── config
│   └── SecurityConfig
├── controller
│   ├── ExchangeRateController
│   ├── SettlementController
│   └── SettlementReportController
├── domain
│   ├── entity
│   │   ├── Currency
│   │   ├── ExchangeRate
│   │   ├── Receivable
│   │   ├── ReceivableType
│   │   ├── Settlement
│   │   └── SettlementStatus
│   ├── repository
│   │   ├── CurrencyRepository
│   │   ├── ExchangeRateRepository
│   │   ├── ReceivableRepository
│   │   └── SettlementRepository
│   └── service
│       ├── ExchangeRateService
│       ├── SettlementReportService
│       ├── SettlementService
│       └── pricing
│           ├── DefaultPricingStrategy
│           └── PricingStrategy
└── CaseDevApplication
```

O projeto também possui o diagrama de entidade-relacionamento em:

```text
docs/er-diagram.md
```

## Funcionalidades

### Cálculo do valor presente

O valor presente é calculado pela estratégia de precificação:

```text
PV = FV / (1 + taxa_base + spread)^prazo
```

Onde:

- `FV` = valor de face do recebível
- `taxa_base` = taxa informada na liquidação
- `spread` = spread do tipo de recebível
- `prazo` = prazo em meses
- `PV` = valor presente

A regra é isolada através de `PricingStrategy`, com implementação em `DefaultPricingStrategy`.

A implementação valida valores inválidos para valor de face, taxa base, spread e prazo.

### Liquidação

O fluxo de liquidação:

1. verifica se o recebível existe;
2. verifica se a moeda de pagamento existe;
3. impede a liquidação de um recebível já liquidado;
4. calcula o valor presente;
5. verifica se é necessária conversão cambial;
6. busca a taxa de câmbio mais recente quando as moedas são diferentes;
7. calcula o valor liquidado;
8. persiste a liquidação.

Quando a moeda do recebível e a moeda de pagamento são iguais, não é necessária taxa de câmbio.

### Conversão cambial

Quando as moedas são diferentes, o sistema busca a taxa de câmbio correspondente.

Exemplo:

```text
Valor presente: R$ 90.000,00
Taxa de câmbio: 5,00

Valor liquidado = 90.000 × 5,00
Valor liquidado = 450.000
```

A taxa utilizada é armazenada na liquidação.

### Relatório de liquidações

O relatório apresenta:

- ID da liquidação;
- ID do recebível;
- cedente;
- moeda do recebível;
- moeda do pagamento;
- valor presente;
- valor liquidado;
- status;
- data da liquidação.

Permite filtros por:

- data inicial;
- data final;
- cedente;
- moeda de pagamento.

Também suporta paginação.

## Endpoints

### Criar liquidação

```http
POST /settlements
```

Parâmetros:

| Parâmetro | Tipo | Obrigatório |
|---|---|---|
| `receivableId` | Long | Sim |
| `paymentCurrencyId` | Long | Sim |
| `baseRate` | BigDecimal | Sim |

Exemplo:

```http
POST /settlements?receivableId=1&paymentCurrencyId=2&baseRate=0.01
```

Retorno esperado:

```text
HTTP 201 Created
```

### Relatório de liquidações

```http
GET /settlements/report
```

Parâmetros:

| Parâmetro | Tipo | Obrigatório | Padrão |
|---|---|---|---|
| `startDate` | LocalDate | Não | — |
| `endDate` | LocalDate | Não | — |
| `assignor` | String | Não | — |
| `currency` | String | Não | — |
| `page` | int | Não | `0` |
| `size` | int | Não | `20` |

Datas devem utilizar o formato:

```text
yyyy-MM-dd
```

Exemplo:

```http
GET /settlements/report?startDate=2026-01-01&endDate=2026-08-10&assignor=ACME&currency=USD&page=0&size=20
```

### Paginação e validações

O serviço valida:

```text
page >= 0
size > 0
size <= 100
startDate <= endDate
```

Mensagens de validação:

```text
Page must be greater than or equal to zero
Size must be greater than zero
Size must not be greater than 100
Start date must not be after end date
```

Filtros de texto são normalizados: valores nulos ou em branco são tratados como ausência de filtro e espaços nas extremidades são removidos.

## Persistência

O projeto utiliza PostgreSQL 16 e Spring Data JPA.

As alterações do schema são gerenciadas pelo Flyway e executadas durante a inicialização da aplicação.

O relatório utiliza `SettlementReportProjection` para retornar somente os campos necessários e uma consulta nativa com filtros, ordenação e `countQuery` para paginação.

## Execução

### Pré-requisitos

- Java 21
- Docker e Docker Compose
- Maven ou Maven Wrapper

Verifique o Java:

```bash
java -version
```

Verifique o Maven:

```bash
mvn -version
```

No Windows, caso o Maven Wrapper esteja disponível:

```powershell
.\mvnw.cmd -version
```

### Banco de dados

Suba o PostgreSQL:

```bash
docker compose up -d
```

Configuração utilizada:

```text
Database: srm_credit
User: postgres
Password: postgres
Port: 5432
```

Verifique o container:

```bash
docker ps
```

Para parar o ambiente:

```bash
docker compose down
```

Para remover também o volume persistente:

```bash
docker compose down -v
```

> `docker compose down -v` remove os dados persistidos do PostgreSQL.

### Aplicação

Com Maven:

```bash
mvn spring-boot:run
```

Ou, no Windows com Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

## Testes

Execute toda a suíte com:

```bash
mvn clean test
```

Ou no Windows:

```powershell
.\mvnw.cmd clean test
```

Os testes cobrem:

- contexto da aplicação;
- cálculo de valor presente;
- validações da estratégia de precificação;
- liquidação na mesma moeda;
- liquidação com conversão cambial;
- recebível inexistente;
- moeda inexistente;
- taxa de câmbio inexistente;
- recebível já liquidado;
- relatório;
- filtros;
- normalização de parâmetros;
- paginação;
- validação de datas.

Resultado atual da suíte:

```text
Tests run: 24
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

## Swagger / OpenAPI

A aplicação utiliza SpringDoc OpenAPI.

Com a aplicação em execução, a interface Swagger pode ser acessada em:

```text
/swagger-ui/index.html
```

A documentação permite visualizar e testar os endpoints da API.

## Segurança

A aplicação possui configuração de Spring Security através de `SecurityConfig`.

A configuração atual é destinada ao ambiente de desenvolvimento. Para produção, devem ser configurados mecanismos de autenticação, autorização e credenciais adequados ao ambiente.

## Decisões de projeto

### Strategy Pattern

O cálculo financeiro é abstraído por:

```text
PricingStrategy
    ↓
DefaultPricingStrategy
```

Isso permite substituir ou adicionar estratégias de precificação sem acoplar a regra diretamente ao fluxo de liquidação.

### Projection no relatório

`SettlementReportProjection` evita o carregamento desnecessário de entidades completas e mantém o retorno do relatório focado nos dados necessários.

### Paginação

O relatório utiliza `Page<SettlementReportProjection>` e `Pageable` do Spring Data.

### Separação de responsabilidades

- **Controllers:** exposição dos endpoints HTTP.
- **Services:** regras de negócio e validações.
- **Repositories:** acesso e consultas aos dados.
- **Entities:** representação do domínio.
- **DTOs/Projections:** contratos de entrada e saída da API.

## Fluxo de liquidação

```text
Cliente
   │
   │ POST /settlements
   ▼
SettlementController
   │
   ▼
SettlementService
   │
   ├── Valida recebível
   ├── Valida moeda de pagamento
   ├── Verifica liquidação existente
   ├── Calcula valor presente
   │       └── PricingStrategy
   ├── Verifica necessidade de câmbio
   ├── Busca ExchangeRate
   ├── Calcula valor liquidado
   └── Persiste Settlement
           │
           ▼
       PostgreSQL
```

## Fluxo do relatório

```text
Cliente
   │
   │ GET /settlements/report
   ▼
SettlementReportController
   │
   ▼
SettlementReportService
   │
   ├── Valida paginação
   ├── Valida datas
   ├── Normaliza filtros
   │
   ▼
SettlementRepository
   │
   ├── Query SQL
   ├── Filtros opcionais
   ├── Ordenação
   └── Paginação
   │
   ▼
SettlementReportProjection
   │
   ▼
Resposta JSON
```

## Observação sobre os endpoints

O endpoint de relatório deve ser exposto por apenas um controller. A configuração recomendada é:

```text
SettlementController
    POST /settlements

SettlementReportController
    GET /settlements/report
```

Evite manter dois métodos/controllers com o mesmo mapping `GET /settlements/report`.

## Status

A suíte automatizada está passando com sucesso:

```text
24 testes
0 falhas
0 erros
```

Projeto desenvolvido como parte do desafio técnico SRM.
