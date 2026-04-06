# Sistema de Estoque Full-Stack

## Descrição

Este projeto é um sistema completo de gerenciamento de estoque desenvolvido com uma arquitetura full-stack. O backend é construído com Spring Boot, utilizando Java 21, e fornece uma API REST para operações CRUD (Criar, Ler, Atualizar, Deletar) de produtos. O sistema permite gerenciar produtos com informações como nome, preço, quantidade e descrição.

Atualmente, o projeto conta com:
- **Backend**: API REST desenvolvida em Spring Boot com Spring Data JPA para persistência de dados.
- **Banco de Dados**: PostgreSQL para armazenamento dos dados de produtos.
- **Testes**: Testes unitários para serviços e controladores utilizando JUnit e Mockito.
- **Tratamento de Erros**: Manipulação global de exceções com respostas adequadas.
- **Documentação**: Código bem comentado e estruturado.

Desenvolvido para fins de estudo e solidificação dos conhecimentos adquiridos no curso **Desenvolvedor Full Stack Java** da EBAC (Escola Britânica de Artes Criativas e Tecnologia). O objetivo é aplicar na prática os conceitos aprendidos em desenvolvimento full-stack, incluindo backend com Spring Boot, testes unitários, boas práticas de código e planejamento de futuras expansões com frontend e infraestrutura.

## Funcionalidades Atuais

- **CRUD de Produtos**:
  - Listar todos os produtos
  - Buscar produto por ID
  - Criar novo produto
  - Atualizar produto existente
  - Deletar produto
- **Validação de Dados**: Utilizando Bean Validation para garantir integridade dos dados.
- **Logs**: Configuração de logs SQL formatados para depuração.

## Tecnologias Utilizadas

### Backend
- **Java 21**
- **Spring Boot 4.0.3**
- **Spring Data JPA**
- **Spring Web**
- **Spring Validation**
- **PostgreSQL**
- **Lombok** (para reduzir boilerplate)
- **JUnit 5** e **Mockito** para testes
- **Maven** para gerenciamento de dependências

### Infraestrutura
- **Banco de Dados**: PostgreSQL rodando em localhost:5432
- **Configuração JPA**: DDL auto-update para criação automática de tabelas

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/sminato/sistemaestoque/
│   │   ├── SistemaEstoqueApplication.java          # Classe principal
│   │   ├── controller/
│   │   │   └── ProdutoController.java              # Endpoints REST
│   │   ├── dto/
│   │   │   ├── ProdutoRequestDTO.java              # DTO para requisições
│   │   │   └── ProdutoResponseDTO.java             # DTO para respostas
│   │   ├── entity/
│   │   │   └── Produto.java                        # Entidade JPA
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java         # Tratamento global de erros
│   │   │   └── ProdutoNotFoundException.java       # Exceção customizada
│   │   ├── repository/
│   │   │   └── IProdutoRepository.java             # Interface do repositório
│   │   └── service/
│   │       └── ProdutoService.java                 # Lógica de negócio
│   └── resources/
│       └── application.properties                  # Configurações da aplicação
└── test/
    └── java/com/sminato/sistemaestoque/
        ├── SistemaEstoqueApplicationTests.java     # Testes de integração
        ├── controller/
        │   └── ProdutoControllerTest.java          # Testes do controlador
        └── service/
            └── ProdutoServiceTest.java             # Testes do serviço
```

## Como Executar

### Pré-requisitos
- **Java 21** instalado
- **Maven** instalado (ou use o wrapper `./mvnw`)
- **PostgreSQL** rodando em localhost:5432 com banco de dados `estoque`
  - Usuário: `postgres`
  - Senha: `413566`

### Passos para Execução
1. Clone o repositório:
   ```bash
   git clone https://github.com/sminato27/Projeto-SistemaEstoque-Full-Stack.git
   cd Projeto-SistemaEstoque-Full-Stack
   ```

2. Execute os testes para validar o código:
   ```bash
   mvn test
   ```

3. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

4. A API estará disponível em `http://localhost:8080`

### Endpoints da API
- `GET /produtos` - Lista todos os produtos
- `GET /produtos/{id}` - Busca produto por ID
- `POST /produtos` - Cria um novo produto
- `PUT /produtos/{id}` - Atualiza um produto existente
- `DELETE /produtos/{id}` - Deleta um produto

Exemplo de requisição POST:
```json
{
  "nome": "Produto Exemplo",
  "preco": 99.99,
  "quantidade": 10,
  "descricao": "Descrição do produto"
}
```

## Funcionalidades Futuras

O projeto será expandido com as seguintes funcionalidades:

- **Frontend com Next.js 14+ (App Router)**:
  - Desenvolvimento de interface de usuário moderna e responsiva.
  - Utilização de Server Components para otimização de performance.
  - Implementação de rotas dinâmicas para navegação fluida.

- **Páginas e Deploy**:
  - Criação de páginas utilizando Server Components.
  - Aplicação de rotas dinâmicas para produtos individuais.
  - Deploy automatizado na plataforma Vercel.

- **Tailwind CSS e Testes**:
  - Implementação de Tailwind CSS para estilização rápida e consistente.
  - Configuração de Jest + React Testing Library para testes de componentes.
  - Execução de testes unitários com `npm test`.

- **Swagger e Autenticação JWT**:
  - Adição de Swagger com SpringDoc OpenAPI para documentação interativa da API.
  - Implementação de autenticação básica JWT com Spring Security.
  - Documentação de endpoints via SwaggerUI com token JWT funcional.

- **CRUD Completo com Autenticação**:
  - Refatoração do CRUD para incluir autenticação JWT obrigatória.
  - Proteção de endpoints com tokens de acesso.

- **Docker Compose**:
  - Configuração de containers para backend, frontend e banco de dados.
  - Facilitação do desenvolvimento e deploy com isolamento de ambientes.

- **Testes Unitários Front e Back**:
  - Expansão da cobertura de testes no backend.
  - Implementação de testes unitários no frontend com Jest e React Testing Library.

- **Swagger Documentado**:
  - Documentação completa da API com exemplos e autenticação.

- **Deploy**:
  - Deploy do frontend na Vercel.
  - Deploy do backend no Railway ou Render.
