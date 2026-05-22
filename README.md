# Bio-State Fermentation Monitor

A full-stack application built with React + Vite for the frontend and Java Spring Boot for the backend. It is designed to capture live culture imagery, send that image data to a backend analysis service, and produce a fermentation status result.

## The Problem Space & Motivation

Home fermentation (Sourdough, Kombucha) is highly variable and historically relies on manual tracking or the "vibe" of the culture. Existing digital trackers fail to account for the physical state of the organism. 

I architected this system to bridge the physical-digital gap. The goal was twofold:

1. **Practical:** Replace subjective visual analysis with deterministic, RAG-informed AI heuristics.

2. **Technical:** Demonstrate how to properly integrate Multimodal LLMs into a strict, enterprise-grade architecture (Java/Spring Boot proxy enforcing Jackson JSON schemas) rather than relying on fragile, client-side API calls.

## Quick Start

1. Start the backend service in one terminal:
   ```bash
   cd backend
   export GEMINI_API_KEY="your_api_key"
   mvn spring-boot:run
   ```
2. Start the frontend in another terminal:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
3. Open the local Vite URL shown in the terminal (usually `http://localhost:5173`).

## Architecture

- **Frontend**: React 19 + Vite + Tailwind CSS + lucide-react.
- **Backend**: Java 17 + Spring Boot 3.2.4 + Spring Security + H2 in-memory database.
- **AI Integration**: The backend dispatches visual fermentation analysis requests to the Google Gemini generative language API.

## AI Features

- Live webcam capture is converted to a base64 JPEG string in `frontend/src/App.jsx`.
- The backend sends the captured image and culture type to Google Gemini in `backend/src/main/java/com/biostate/monitor/FermentationAnalyzer.java`.
- The AI is prompted with sourdough and kombucha fermentation heuristics and returns a structured JSON result.
- The response format includes:
  - `status`
  - `confidence`
  - `visual_observations`
  - `rag_reference`
  - `actionable_advice`
- Analysis results are persisted in the backend H2 database for later retrieval.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Node.js 18 or higher
- A valid Google Gemini API key

## Environment Variables

This project uses environment variables to keep secrets and ports separate from source code.

- `GEMINI_API_KEY` - required by the backend to authenticate with Google Gemini.
- `BACKEND_PORT` - optional local backend port (default `8080`).
- `FRONTEND_PORT` - optional local frontend port (default `5173`).

You can use `.env.example` as a template for local setup.

## Backend Setup

1. Open a terminal and change into the backend folder:

```bash
cd backend
```

2. Set the Gemini API key as an environment variable:

- macOS / Linux:
  ```bash
  export GEMINI_API_KEY="your_api_key"
  ```
- Windows CMD:
  ```cmd
  set GEMINI_API_KEY="your_api_key"
  ```
- Windows PowerShell:
  ```powershell
  $env:GEMINI_API_KEY="your_api_key"
  ```

3. Start the backend service:

```bash
mvn spring-boot:run
```

The backend starts on `http://localhost:8080` by default.

## Frontend Setup

1. Open another terminal and change into the frontend folder:

```bash
cd frontend
```

2. Install dependencies:

```bash
npm install
```

3. Start the Vite development server:

```bash
npm run dev
```

4. Open the local URL shown in the terminal (typically `http://localhost:5173`).

## API Behavior

- The frontend is configured to call the backend endpoint at `http://localhost:8080/api/fermentation/analyze`.
- The backend requires authenticated requests for analysis and expects a JSON body containing:
  - `base64Image` (string)
  - `cultureType` (string)
  - `cultureId` (number)
- Authentication is handled via JWTs, and auth endpoints are available under `/auth`.
- The backend uses an in-memory H2 database for users, cultures, and analyses.

### Example Analyze Request

```json
{
  "base64Image": "<base64 JPEG data>",
  "cultureType": "sourdough",
  "cultureId": 1
}
```

## Authentication Flow

1. Register a user via `POST /auth/register` with `username`, `email`, and `password`.
2. Log in via `POST /auth/login` with `username` and `password`.
3. Receive a JWT token from the login response.
4. Attach the JWT to authenticated requests using the `Authorization: Bearer <token>` header.

## User Guide

- **Choose culture type** before capture: select `sourdough` or `kombucha` so the AI uses the right fermentation heuristics.
- **Keep the frame stable** and use even lighting. Avoid glare or heavy shadows on the culture.
- **Wait for the backend**: capture sends the frame to Spring Boot, then the app displays analysis results once Gemini returns them.
- **Result sections**:
  - `status` shows fermentation health.
  - `confidence` indicates how sure the AI is.
  - `visual_observations` explains what the model saw.
  - `actionable_advice` gives the next steps.

## Documentation & User Experience

This repository includes:

- A well-organized `README.md` with setup steps, API behavior, and auth flow.
- A `.env.example` file for easy local configuration.
- In-app UX guidance inside the frontend to help users capture better images and understand results.
- Clear error messages when the camera or backend connection fails.

## Security & Compliance

- The backend uses **JWT authentication** for protected API routes and **BCrypt** for password hashing.
- User inputs are validated before registration to enforce safe usernames, proper email formats, and strong passwords.
- The API uses **CORS restrictions** to allow requests only from `http://localhost:5173` in development.
- Sensitive secrets like `GEMINI_API_KEY` should always be stored outside source control and loaded from environment variables.
- Security headers such as **Content-Security-Policy** and **X-Content-Type-Options** are enabled in the backend.

## Notes

- Do not store your Gemini API key in source control.
- If the frontend is unable to reach the backend, ensure the Spring Boot server is running on port `8080`.
- The backend currently secures all non-`/auth` endpoints with JWT-based authentication.

## Development Commands

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Docker & Deployment

### Build and Run with Docker Compose

```bash
export GEMINI_API_KEY="your_api_key"
docker-compose up --build
```

This starts both the backend (port 8080) and frontend (port 5173) with health checks.

### API Documentation

Swagger UI is available at `http://localhost:8080/swagger-ui.html` when the backend is running.

## CI/CD Pipeline

GitHub Actions automatically runs tests and builds on every push to `main`:
- Backend: Maven build, unit tests, and compilation checks
- Frontend: ESLint, Vite build, and Jest tests

See `.github/workflows/ci-cd.yml` for configuration.

## Known Limitations

- The frontend currently sends image and culture type data, but the backend also expects a valid authenticated culture context (`cultureId`).
- The backend is configured to use H2 in-memory persistence, which is reset each time the service restarts.
