# Changelog

All notable changes to the Bio-State Fermentation Monitor project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Comprehensive testing framework (Jest for frontend, JUnit for backend)
- Spring Boot Actuator for health checks and metrics monitoring
- Structured logging with timestamps and thread information
- API documentation via Swagger UI at `/swagger-ui.html`
- Input validation utilities (ValidationUtil, ImageValidationUtil)
- Global exception handler for centralized error handling
- Docker multi-stage builds for optimized container images
- GitHub Actions CI/CD pipeline for automated testing and building
- Security headers (CSP, XSS Protection, HSTS, Content-Type-Options)
- CORS hardening restricted to development frontend origin

### Changed
- Frontend uses Vite 8.0.10 (from implicit version)
- Backend upgraded to Spring Boot 4.0.6 (from 3.2.4)
- Java version set to 25 (from 17)
- Improved App.jsx useEffect handling for proper async camera initialization
- Enhanced error messages and user guidance in UI
- ESLint configuration includes Jest environment globals

### Fixed
- Frontend CI/CD linting errors (unused variables, improper async handling)
- Removed unused 'err' variable in catch block
- Fixed setState synchronously called in useEffect warning

### Security
- Added BCrypt password hashing for user authentication
- Implemented JWT bearer token authentication
- Input validation for usernames, emails, and passwords
- Image validation with MIME type and size constraints (5MB max)
- Secure headers for XSS and clickjacking protection

## [0.0.1] - 2026-05-31

### Added
- Initial project setup with React + Vite frontend and Spring Boot backend
- Live webcam capture and fermentation analysis
- JWT-based authentication (register/login)
- Integration with Google Gemini API for AI analysis
- H2 in-memory database for data persistence
- Tailwind CSS styling with lucide-react icons
- RESTful API endpoints for authentication, culture management, and analysis
- Health checks and monitoring via Spring Boot Actuator
- Docker and docker-compose configuration for containerized deployment
- Makefile for convenient development commands
- Comprehensive documentation (README, ARCHITECTURE, CONTRIBUTING)

### Features
- **Frontend**: Real-time webcam feed with culture type selection
- **Backend**: JWT authentication, culture management, fermentation analysis
- **AI Integration**: Google Gemini multimodal vision for fermentation assessment
- **Testing**: Unit tests for core services and components
- **CI/CD**: Automated GitHub Actions pipeline for testing and building
- **Deployment**: Docker containers with health checks and service dependencies
