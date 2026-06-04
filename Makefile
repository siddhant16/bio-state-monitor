.PHONY: help install dev test build lint clean docker-build docker-up docker-down

help:
	@echo "Bio-State Fermentation Monitor - Makefile Commands"
	@echo ""
	@echo "Installation & Setup:"
	@echo "  make install          Install all dependencies (frontend & backend)"
	@echo "  make install-fe       Install frontend dependencies"
	@echo "  make install-be       Install backend dependencies"
	@echo ""
	@echo "Development:"
	@echo "  make dev              Start development servers (frontend & backend)"
	@echo "  make dev-fe           Start frontend development server only"
	@echo "  make dev-be           Start backend development server only"
	@echo ""
	@echo "Testing:"
	@echo "  make test             Run all tests (frontend & backend)"
	@echo "  make test-fe          Run frontend tests with Jest"
	@echo "  make test-be          Run backend tests with Maven"
	@echo ""
	@echo "Code Quality:"
	@echo "  make lint             Lint all code (frontend & backend)"
	@echo "  make lint-fe          Lint frontend code with ESLint"
	@echo ""
	@echo "Building:"
	@echo "  make build            Build both frontend and backend"
	@echo "  make build-fe         Build frontend for production"
	@echo "  make build-be         Build backend JAR"
	@echo ""
	@echo "Docker:"
	@echo "  make docker-build     Build Docker images"
	@echo "  make docker-up        Start Docker containers"
	@echo "  make docker-down      Stop Docker containers"
	@echo "  make docker-clean     Remove Docker images and containers"
	@echo ""
	@echo "Cleanup:"
	@echo "  make clean            Clean build artifacts and caches"
	@echo "  make clean-fe         Clean frontend artifacts"
	@echo "  make clean-be         Clean backend artifacts"

# Installation targets
install: install-fe install-be

install-fe:
	cd frontend && npm install

install-be:
	cd backend && mvn clean install

# Development targets
dev:
	@echo "Starting frontend and backend dev servers..."
	@echo "Frontend: http://localhost:5173"
	@echo "Backend: http://localhost:8080"
	cd frontend && npm run dev & cd backend && mvn spring-boot:run

dev-fe:
	cd frontend && npm run dev

dev-be:
	cd backend && mvn spring-boot:run

# Testing targets
test: test-fe test-be

test-fe:
	cd frontend && npm test

test-be:
	cd backend && mvn test

# Linting targets
lint: lint-fe

lint-fe:
	cd frontend && npm run lint

# Building targets
build: build-fe build-be

build-fe:
	cd frontend && npm run build

build-be:
	cd backend && mvn clean package

# Docker targets
docker-build:
	docker-compose build

docker-up:
	docker-compose up

docker-down:
	docker-compose down

docker-clean: docker-down
	docker-compose down -v
	docker image prune -f

# Cleanup targets
clean: clean-fe clean-be

clean-fe:
	cd frontend && rm -rf dist node_modules/.vite build coverage

clean-be:
	cd backend && mvn clean

# Development environment setup
setup:
	@echo "Setting up development environment..."
	@if [ ! -f .env ]; then \
		cp .env.example .env; \
		echo ".env file created. Please update it with your GEMINI_API_KEY"; \
	else \
		echo ".env already exists"; \
	fi
	@echo "✓ Setup complete. Run 'make dev' to start development servers."

# Format and quality checks
format-check:
	@echo "Checking code formatting..."
	cd frontend && npm run lint

verify: lint test
	@echo "✓ All verifications passed"
