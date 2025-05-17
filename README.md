# Contactfy API

API para o sistema de Agendamento Telefônico **Contactfy**, desenvolvida em Java com Spring Boot.

## 📋 Funcionalidades

* Cadastro de contatos
* Consulta e listagem de contatos
* Atualização de dados dos contatos
* Inativação de contatos
* Marcar e desmarcar contatos como favoritos
* Comunicação via API REST
* Banco de dados PostgreSQL
* Geração automática de tabelas via Flyway
* Documentação interativa com Swagger

## 🚀 Tecnologias utilizadas

* Java 21
* Spring Boot
* Spring Web
* Spring Security
* Spring Data JPA
* Flyway
* PostgreSQL
* Lombok
* Swagger (Springdoc OpenAPI)

## 🗂️ Documentação da API

* A documentação da API está disponível via Swagger:

  ```
  http://localhost:8080/swagger-ui/index.html
  ```
* A collection do Postman e o arquivo OpenAPI (JSON) estão localizados em:

  ```
  src/main/resources/api/
  ```

## 🏁 Como rodar o projeto

1. Clone o repositório:

   ```bash
   git clone git@github.com:seu-usuario/contactfy-api.git
   ```

2. Crie um banco de dados PostgreSQL com o nome `contactfy` e schema `desafio`.

3. Configure as variáveis de ambiente (pode ser via `.env`, configuração do sistema ou run configuration):

### ✅ Variáveis de ambiente disponíveis

| Variável                          | Descrição                        | Valor padrão                                                       |
| --------------------------------- | -------------------------------- | ------------------------------------------------------------------ |
| `CONTACTFY_SERVER_PORT`           | Porta da aplicação               | `8080`                                                             |
| `CONTACTFY_DB_DRIVER_CLASS_NAME`  | Driver JDBC                      | `org.postgresql.Driver`                                            |
| `CONTACTFY_DB_URL`                | URL do banco                     | `jdbc:postgresql://localhost:5432/contactfy?currentSchema=desafio` |
| `CONTACTFY_DB_USERNAME`           | Usuário do banco                 | `root`                                                             |
| `CONTACTFY_DB_PASSWORD`           | Senha do banco                   | *(obrigatório se não usar padrão)*                                 |
| `CONTACTFY_JPA_SHOW_SQL`          | Exibir queries no console        | `false`                                                            |
| `CONTACTFY_JWT_SECRET`            | Chave secreta para JWT           | `!C0nt4ctFyS3cr3t@2025!`                                           |
| `CONTACTFY_JWT_EXPIRATION`        | Tempo de expiração do token (ms) | `1800000` (30 minutos)                                             |
| `CONTACTFY_HIBERNATE_LOG_LEVEL`   | Nível de log do Hibernate        | `WARN`                                                             |
| `CONTACTFY_SPRING_WEB_LOG_LEVEL`  | Nível de log do Spring Web       | `WARN`                                                             |
| `CONTACTFY_SPRING_SECURITY_LEVEL` | Nível de log do Spring Security  | `WARN`                                                             |

4. Compile e execute o projeto com Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

5. A API estará disponível em:

   ```
   http://localhost:8080
   ```

## 🔐 Autenticação

O sistema utiliza JWT para autenticação. Após autenticar via `/auth`, inclua o token nos headers das requisições:

```http
Authorization: Bearer SEU_TOKEN_AQUI
```
