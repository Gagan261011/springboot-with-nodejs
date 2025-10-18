# springboot-with-nodejs

User Management Suite – a production-ready full-stack CRUD application built with Angular and Spring Boot.

## Tech Stack

- **Backend:** Spring Boot 3, Java 17, Spring Data JPA, Validation, MapStruct, Lombok, Flyway, Springdoc OpenAPI
- **Frontend:** Angular 17, Angular Material, RxJS, Reactive Forms, ESLint, Prettier
- **Database:** H2 (dev), PostgreSQL (prod)
- **Tooling:** Maven, npm, Docker, docker compose

## Project Structure

```
project-root/
|-- backend/             # Spring Boot service
|-- frontend/            # Angular web client
|-- postman/             # Postman collection
|-- docker-compose.yml
|-- .env.example
|-- README.md
```

## Prerequisites

- JDK 17+
- Maven 3.9+
- Node.js 20 (LTS) + npm
- Docker & Docker Compose (for containerised flow)

## Quick Start (Local Development)

### 1. Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend URLs:

- Swagger UI: http://localhost:8095/swagger-ui.html
- OpenAPI JSON: http://localhost:8095/api-docs
- H2 Console: http://localhost:8095/h2-console (JDBC URL `jdbc:h2:file:./data/userdb`)

### 2. Frontend (Angular)

```bash
cd frontend
npm install
npm start
```

Angular dev server with proxy: http://localhost:4201

## Running Tests

- Backend unit/integration tests:

  ```bash
  cd backend
  mvn clean verify
  ```

- Frontend unit tests:

  ```bash
  cd frontend
  npm test
  ```

- Lint & format:

  ```bash
  cd frontend
  npm run lint
  npm run format
  ```

## Dockerised Environment

1. Copy `.env.example` to `.env` and adjust values if needed.

   ```bash
   cp .env.example .env
   ```

2. Build and start the stack:

   ```bash
   docker compose up --build
   ```

Services:

- Frontend: http://localhost:4201
- API: http://localhost:8095
- PostgreSQL: localhost:5432

To stop and remove containers:

```bash
docker compose down
```

## Docker / Compose (Dev & Prod)

Quick start (dev): copy example env and bring up the stack with hot-reload for frontend and backend:

```bash
cp .env.example .env
make up
```

Production (build optimised images and run behind nginx):

```bash
cp .env.example .env
make build-prod
make up-prod
```

Troubleshooting:
- If the backend fails to start, check `docker compose logs api` and ensure DB env vars in `.env` match the DB service.
- To remove volumes and rebuild completely: `make down && make prune && make up`.

## Useful npm Scripts

| Script        | Description                  |
| ------------- | ---------------------------- |
| `npm start`   | Run Angular app with proxy   |
| `npm build`   | Production build             |
| `npm test`    | Execute unit tests           |
| `npm lint`    | Run ESLint                   |
| `npm format`  | Format source with Prettier  |

## Useful Maven Commands

| Command                                    | Description                 |
| ------------------------------------------ | --------------------------- |
| `mvn clean verify`                         | Build and test              |
| `mvn spring-boot:run -Dspring-boot.run.profiles=dev` | Run dev profile backend |

## API Examples

```bash
# Create a user
curl -X POST http://localhost:8095/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
        "firstName": "Grace",
        "lastName": "Hopper",
        "email": "grace.hopper@example.com",
        "role": "ADMIN",
        "status": "ACTIVE"
      }'

# List users
curl "http://localhost:8095/api/v1/users?page=0&size=10"
```

## Postman

Import `postman/UserManagement.postman_collection.json` for ready-to-use CRUD requests.

## Additional Notes

- Default dev profile seeds five sample users.
- Soft deletes mark users as `INACTIVE`.
- Global error model: `{ timestamp, path, status, code, message, details[] }`.
- Update `.env` when deploying to production.
