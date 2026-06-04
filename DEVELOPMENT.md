# Development Guide

Comprehensive development guide for Bio-State Fermentation Monitor.

## Prerequisites

### System Requirements

- **OS**: macOS, Linux, or Windows (with WSL2)
- **RAM**: 8GB minimum (16GB recommended)
- **Disk**: 10GB free space

### Required Tools

#### Java & Maven (Backend)

```bash
# macOS
brew install java@25 maven

# Linux (Ubuntu/Debian)
sudo apt-get install openjdk-25-jdk maven

# Verify installation
java -version
mvn -version
```

#### Node.js & npm (Frontend)

```bash
# macOS
brew install node@20

# Linux (Ubuntu/Debian)
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install nodejs

# Verify installation
node --version
npm --version
```

#### Docker (Optional, for containerized development)

```bash
# Download from https://www.docker.com/products/docker-desktop
docker --version
docker-compose --version
```

#### Git

```bash
# macOS
brew install git

# Linux
sudo apt-get install git

# Windows
# Download from https://git-scm.com/
```

## Project Structure

```
bio-state-monitor/
├── backend/                          # Spring Boot backend
│   ├── src/
│   │   ├── main/java/com/biostate/monitor/
│   │   │   ├── BioStateApplication.java       # Main entry point
│   │   │   ├── FermentationAnalyzer.java      # Gemini API integration
│   │   │   ├── FermentationController.java    # API endpoint
│   │   │   ├── controller/                    # REST controllers
│   │   │   ├── model/                         # Data models (User, Culture, Analysis)
│   │   │   ├── repository/                    # JPA repositories
│   │   │   ├── security/                      # JWT & security config
│   │   │   └── service/                       # Business logic
│   │   ├── resources/
│   │   │   └── application.properties         # Spring config
│   │   └── test/                              # Unit tests
│   ├── pom.xml                                # Maven configuration
│   └── Dockerfile                             # Multi-stage Docker build
├── frontend/                         # React + Vite frontend
│   ├── src/
│   │   ├── App.jsx                            # Main component
│   │   ├── App.test.jsx                       # Component tests
│   │   ├── main.jsx                           # Entry point
│   │   ├── setupTests.js                      # Jest configuration
│   │   ├── assets/                            # Static assets
│   │   ├── App.css                            # Component styles
│   │   └── index.css                          # Global styles
│   ├── package.json                           # npm dependencies
│   ├── vite.config.js                         # Vite configuration
│   ├── eslint.config.js                       # Linting rules
│   ├── jest.config.cjs                        # Jest configuration
│   ├── tailwind.config.js                     # Tailwind CSS config
│   ├── postcss.config.js                      # PostCSS config
│   └── Dockerfile                             # Multi-stage Docker build
├── .github/
│   └── workflows/
│       └── ci-cd.yml                          # GitHub Actions pipeline
├── docker-compose.yml                         # Container orchestration
├── Makefile                                   # Development commands
├── ARCHITECTURE.md                            # System design
├── API.md                                     # API documentation
├── DEPLOYMENT.md                              # Production guide
├── SECURITY.md                                # Security policy
└── CONTRIBUTING.md                            # Contribution guidelines
```

## Initial Setup

### 1. Clone Repository

```bash
git clone https://github.com/siddhant16/bio-state-monitor.git
cd bio-state-monitor
```

### 2. Configure Environment

```bash
# Copy environment template
cp .env.example .env

# Edit .env with your configuration
# Required: GEMINI_API_KEY
nano .env  # or use your editor
```

### 3. Install Dependencies

**Option A: Using Makefile (Recommended)**

```bash
make install
```

**Option B: Manual Installation**

```bash
# Frontend
cd frontend
npm install
cd ..

# Backend
cd backend
mvn clean install
cd ..
```

## Development Workflow

### Starting Development Servers

**Option A: Using Makefile**

```bash
# Start both frontend and backend
make dev

# Or start individually
make dev-fe  # Frontend only
make dev-be  # Backend only
```

**Option B: Manual Setup**

```bash
# Terminal 1: Backend
cd backend
export GEMINI_API_KEY="your_api_key"
mvn spring-boot:run

# Terminal 2: Frontend
cd frontend
npm run dev
```

**Option C: Docker Compose**

```bash
docker-compose up --build
```

### Access Points

- **Frontend**: http://localhost:5173
- **Backend**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

## Code Organization

### Backend Structure

```
backend/src/main/java/com/biostate/monitor/
├── BioStateApplication.java         # @SpringBootApplication entry point
├── FermentationAnalyzer.java        # Calls Gemini API
├── FermentationController.java      # POST /api/fermentation/analyze
│
├── controller/
│   ├── AuthController.java          # /auth endpoints
│   ├── AnalysisController.java      # /api/analyses endpoints
│   └── CultureController.java       # /api/cultures endpoints
│
├── model/
│   ├── User.java                    # @Entity User with BCrypt password
│   ├── Culture.java                 # @Entity Culture (sourdough/kombucha)
│   └── Analysis.java                # @Entity Analysis result
│
├── repository/
│   ├── UserRepository.java          # Spring Data JPA repository
│   ├── CultureRepository.java
│   └── AnalysisRepository.java
│
├── security/
│   ├── JwtUtil.java                 # Token generation/validation
│   ├── JwtAuthenticationFilter.java  # Filter for JWT validation
│   └── SecurityConfig.java          # Spring Security configuration
│
├── service/
│   ├── UserService.java             # Registration/lookup logic
│   ├── CultureService.java          # Culture CRUD operations
│   └── AnalysisService.java         # Analysis persistence
│
└── util/
    ├── ValidationUtil.java          # Input validation (username, email, password)
    └── ImageValidationUtil.java     # Image format/size validation
```

### Frontend Structure

```
frontend/src/
├── App.jsx                  # Main component (webcam, capture, analysis)
├── App.test.jsx             # Component unit tests
├── main.jsx                 # React root entry
├── setupTests.js            # Jest setup file
├── index.css                # Global styles
├── App.css                  # Component-specific styles
└── assets/                  # Images, icons, etc.
```

## Building & Testing

### Frontend Testing

```bash
cd frontend

# Run linting
npm run lint

# Run tests
npm test

# Build for production
npm run build

# Preview production build
npm run preview
```

### Backend Testing

```bash
cd backend

# Run tests
mvn test

# Build JAR
mvn clean package

# Run with Spring Boot plugin
mvn spring-boot:run
```

### Full Test Suite

```bash
# Using Makefile
make test
make lint
make build

# Manual
make test-fe && make test-be
make lint-fe
make build-fe && make build-be
```

## Database

### Current Setup (Development)

- **Type**: H2 in-memory
- **Location**: `/tmp/test` (configurable)
- **Data Persistence**: Lost on restart

### Accessing H2 Console

```
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave blank)
```

### Schema

**User Table**
```sql
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

**Culture Table**
```sql
CREATE TABLE culture (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  type VARCHAR(20) NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id)
);
```

**Analysis Table**
```sql
CREATE TABLE analysis (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  culture_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,
  confidence DOUBLE,
  observations TEXT,
  advice TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (culture_id) REFERENCES culture(id)
);
```

## Debugging

### Backend Debugging

#### Enable Debug Mode

```bash
# Option 1: Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"

# Option 2: Java
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar
```

#### VS Code Configuration

Create `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Java Debugger",
      "type": "java",
      "name": "Launch bio-state-monitor",
      "request": "launch",
      "mainClass": "com.biostate.monitor.BioStateApplication",
      "projectName": "monitor",
      "cwd": "${workspaceFolder}/backend"
    }
  ]
}
```

### Frontend Debugging

#### VS Code Extensions

- Debugger for Firefox
- Debugger for Chrome
- ES7+ React/Redux/React-Native snippets

#### Debug Configuration

Create `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "launch",
      "name": "Launch Chrome",
      "url": "http://localhost:5173",
      "webRoot": "${workspaceFolder}/frontend/src"
    }
  ]
}
```

### Logging

#### Backend

```properties
# application.properties
logging.level.com.biostate.monitor=DEBUG
logging.level.org.springframework.security=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

#### Frontend

```javascript
// Enable verbose logging in development
if (process.env.NODE_ENV === 'development') {
  console.log('Detailed debug info...');
}
```

## Common Development Tasks

### Adding a New API Endpoint

1. **Create Controller Method**
   ```java
   @PostMapping("/api/new-endpoint")
   public ResponseEntity<?> newEndpoint(@RequestBody SomeRequest request) {
       // Implementation
       return ResponseEntity.ok(response);
   }
   ```

2. **Add Unit Test**
   ```java
   @Test
   public void testNewEndpoint() {
       // Arrange
       SomeRequest request = new SomeRequest();
       
       // Act
       ResponseEntity<?> response = controller.newEndpoint(request);
       
       // Assert
       assertEquals(HttpStatus.OK, response.getStatusCode());
   }
   ```

3. **Test with cURL**
   ```bash
   curl -X POST http://localhost:8080/api/new-endpoint \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{"field": "value"}'
   ```

### Adding Frontend Component

1. **Create Component**
   ```javascript
   // src/components/NewComponent.jsx
   export default function NewComponent() {
       return <div>New Component</div>;
   }
   ```

2. **Add Tests**
   ```javascript
   // src/components/NewComponent.test.jsx
   import { render, screen } from '@testing-library/react';
   import NewComponent from './NewComponent';

   test('renders component', () => {
       render(<NewComponent />);
       expect(screen.getByText('New Component')).toBeInTheDocument();
   });
   ```

3. **Import in App.jsx**
   ```javascript
   import NewComponent from './components/NewComponent';
   ```

### Running Specific Tests

```bash
# Backend - specific test class
mvn test -Dtest=UserServiceTest

# Backend - specific test method
mvn test -Dtest=UserServiceTest#testRegisterUser_Success

# Frontend - specific test file
npm test -- App.test.jsx

# Frontend - specific test
npm test -- App.test.jsx -t "renders the app"
```

## Code Quality

### ESLint (Frontend)

```bash
cd frontend

# Check
npm run lint

# Fix auto-fixable issues
npx eslint . --fix
```

### Formatting

```bash
# Using Prettier (if configured)
npx prettier --write src/
```

### Code Coverage

```bash
# Frontend
npm test -- --coverage

# Backend
mvn jacoco:report
# View: target/site/jacoco/index.html
```

## Common Issues & Solutions

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080
# Kill process
kill -9 <PID>

# Or change port
BACKEND_PORT=8081 mvn spring-boot:run
```

### GEMINI_API_KEY Not Found

```bash
# Verify environment variable
echo $GEMINI_API_KEY

# Set it
export GEMINI_API_KEY="your_key"

# Or in .env file
echo "GEMINI_API_KEY=your_key" > .env
```

### npm/Maven Cache Issues

```bash
# Clear npm cache
npm cache clean --force

# Clear Maven cache
rm -rf ~/.m2/repository

# Reinstall
npm install
mvn clean install
```

### Docker Issues

```bash
# Stop all containers
docker-compose down

# Rebuild images
docker-compose build --no-cache

# Restart
docker-compose up
```

## Performance Profiling

### Backend

```bash
# Monitor memory/CPU
jps -l
jstat -gc <pid> 1000

# Create heap dump
jmap -dump:live,format=b,file=heap.bin <pid>
```

### Frontend

- Use Chrome DevTools Performance tab
- Profile components with React DevTools
- Analyze bundle size: `npm run build` shows sizes

## IDE Setup

### VS Code Extensions

- **Backend**: Extension Pack for Java, Spring Boot Extension Pack
- **Frontend**: ES7+ React/Redux/React-Native, Prettier, ESLint
- **Both**: GitLens, Thunder Client (REST client)

### IntelliJ IDEA

- Built-in Java/Maven/Spring support
- React plugin from JetBrains
- Node.js & npm plugin

## Git Workflow

```bash
# Create feature branch
git checkout -b feature/my-feature

# Make changes, commit
git add .
git commit -m "feat: add new feature"

# Push to remote
git push origin feature/my-feature

# Create pull request on GitHub
# After review and approval, merge to main
```

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [Google Gemini API Documentation](https://ai.google.dev)
- [JWT Best Practices](https://tools.ietf.org/html/rfc7519)
- [OWASP Security Guidelines](https://owasp.org/)

## Getting Help

- Check existing issues on GitHub
- Review CONTRIBUTING.md for contribution guidelines
- Email: development@example.com
- Discussions: GitHub Discussions

---

**Last Updated**: 2026-06-04
