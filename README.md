# Bio-State Fermentation Monitor

A full-stack application built with React + Vite for the frontend and Java Spring Boot for the backend. It is designed to capture live culture imagery, send that image data to a backend analysis service, and produce a fermentation status result.

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

## Known Limitations

- The frontend currently sends image and culture type data, but the backend also expects a valid authenticated culture context (`cultureId`).
- The backend is configured to use H2 in-memory persistence, which is reset each time the service restarts.
