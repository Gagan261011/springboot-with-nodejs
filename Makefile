SHELL := /bin/bash

.PHONY: build up down logs ps prune seed test build-prod up-prod

build:
	docker compose build --parallel

up:
	docker compose up --build

down:
	docker compose down

logs:
	docker compose logs -f

ps:
	docker compose ps

prune:
	docker system prune -af --volumes

seed:
	@echo "Seeding DB (if seed scripts are configured)..."

test:
	@echo "Running backend tests"
	cd backend && mvn test

build-prod:
	docker compose -f docker-compose.yml --profile prod build

up-prod:
	docker compose -f docker-compose.yml --profile prod up -d
