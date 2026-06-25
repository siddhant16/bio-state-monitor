# VoiceBite

VoiceBite is a voice-driven calorie tracking assistant that makes nutrition logging easier by combining voice commands with AI.

## Features

- Voice-based meal entry using browser speech recognition
- AI-powered command parsing for calorie and nutrition tracking
- Simple dashboard to review recent voice entries
- Easy local setup with Vite + React

## Getting Started

### Install dependencies

```bash
cd VoiceBite
npm install
```

### Run locally

```bash
npm run dev
```

Open the local URL in your browser.

## How it works

1. Press **Start Listening** to capture your voice input.
2. Your command appears as text.
3. Submit to get AI-assisted calorie parsing and tracking.

## Notes

- This project uses the Web Speech API for voice capture.
- AI integration is designed to be wired to a server or directly to an AI endpoint.
- Replace the placeholder processor with your preferred AI service.
