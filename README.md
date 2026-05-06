# Bio-State Fermentation Monitor

A full-stack application leveraging Multimodal Vision (Gemini 2.5 Flash Vision) to analyze the biological state of sourdough and kombucha cultures via live imagery.

## Architecture

- **Frontend**: React (Vite) + Tailwind CSS + lucide-react. Handles webcam capture, image to base64 encoding, and UI rendering.
- **Backend**: Java 17 + Spring Boot. Acts as a secure proxy, storing the API key, constructing strict JSON payload schemas, and executing HTTP requests to the Gemini API.

## Prerequisites

You must have the following installed on your system to run this codebase locally:

- Java 17 or higher
- Maven 3.6 or higher
- Node.js 18 or higher
- An active Google Gemini API Key

## Setup & Execution

You must run the backend and frontend simultaneously in separate terminal windows. The frontend relies entirely on the backend running on port 8080.

### 1. Backend Environment

The Java backend requires the Gemini API key to be injected via environment variables. Do not hardcode your API key into the Java files, and do not commit it to version control.

Open a terminal and navigate to the backend directory:

```bash
cd backend
```

Set the required environment variable:

- **Mac/Linux**: `export GEMINI_API_KEY="your_api_key"`
- **Windows (CMD)**: `set GEMINI_API_KEY="your_api_key"`
- **Windows (PowerShell)**: `$env:GEMINI_API_KEY="your_api_key"`

Start the Spring Boot server:

```bash
mvn spring-boot:run
```

Verify that the console outputs that the server is running on http://localhost:8080.

### 2. Frontend Environment

Open a second terminal and navigate to the frontend directory:

```bash
cd frontend
```

Install the required Node dependencies:

```bash
npm install
```

Start the Vite development server:

```bash
npm run dev
```

Open the localhost URL provided by Vite in your browser.

## Usage Guidelines

1. Grant the browser permission to access your webcam.
2. Select the appropriate culture profile ("Sourdough" or "Kombucha").
3. Ensure the physical culture is well-lit. The AI relies strictly on visual evidence; poor lighting yields inaccurate inferences.
4. Click "Capture & Run Inference" to dispatch the payload to the Java API.