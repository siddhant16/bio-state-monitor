# API Documentation

Complete reference for Bio-State Fermentation Monitor API endpoints.

## Base URLs

- **Development**: `http://localhost:8080`
- **Production**: Set via environment variables
- **Swagger UI**: `/swagger-ui.html` (interactive documentation)

## Authentication

All protected endpoints require a JWT bearer token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

Tokens are obtained by registering a user or logging in via the `/auth` endpoints.

---

## Authentication Endpoints

### Register User

Create a new user account.

**Endpoint**: `POST /auth/register`

**Request Body**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Validation Rules**:
- Username: 3-30 alphanumeric characters and underscores
- Email: Valid email format
- Password: 8+ characters with uppercase, lowercase, digit, and special character

**Response** (201 Created):
```json
{
  "message": "User registered successfully",
  "userId": 1
}
```

**Error Responses**:
- `400 Bad Request`: Missing required fields or validation failed
- `400 Bad Request`: Username or email already exists

---

### Login User

Authenticate and receive a JWT token.

**Endpoint**: `POST /auth/login`

**Request Body**:
```json
{
  "username": "string",
  "password": "string"
}
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

**Error Responses**:
- `400 Bad Request`: Missing username or password
- `401 Unauthorized`: Invalid credentials

---

## Culture Endpoints

### Get All Cultures

Retrieve all cultures for the authenticated user.

**Endpoint**: `GET /api/cultures`

**Authentication**: Required (JWT Bearer token)

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "name": "My Sourdough",
    "type": "sourdough",
    "userId": 1,
    "createdAt": "2026-05-31T10:30:00Z"
  }
]
```

---

### Create Culture

Create a new fermentation culture.

**Endpoint**: `POST /api/cultures`

**Authentication**: Required (JWT Bearer token)

**Request Body**:
```json
{
  "name": "string",
  "type": "sourdough | kombucha"
}
```

**Response** (201 Created):
```json
{
  "id": 1,
  "name": "My Sourdough",
  "type": "sourdough",
  "userId": 1,
  "createdAt": "2026-05-31T10:30:00Z"
}
```

**Error Responses**:
- `400 Bad Request`: Invalid culture type or missing fields
- `401 Unauthorized`: Invalid or missing JWT token

---

## Analysis Endpoints

### Run Fermentation Analysis

Send an image for AI-powered fermentation analysis.

**Endpoint**: `POST /api/fermentation/analyze`

**Authentication**: Required (JWT Bearer token)

**Request Body**:
```json
{
  "base64Image": "data:image/jpeg;base64,...",
  "cultureType": "sourdough | kombucha",
  "cultureId": 1
}
```

**Validation**:
- Image must be valid base64 JPEG/PNG/WebP
- Maximum image size: 5MB
- Culture type must be "sourdough" or "kombucha"
- Culture ID must correspond to user's culture

**Response** (200 OK):
```json
{
  "status": "healthy | at-risk | complete",
  "confidence": 0.85,
  "visual_observations": "Clear, consistent bubbles indicating active fermentation...",
  "rag_reference": "Active fermentation typically shows consistent CO2 production...",
  "actionable_advice": "Continue current fermentation process. Check again in 24 hours.",
  "timestamp": "2026-05-31T10:35:00Z"
}
```

**Error Responses**:
- `400 Bad Request`: Invalid image format, culture type, or cultureId
- `401 Unauthorized`: Invalid or missing JWT token
- `500 Internal Server Error`: Gemini API error or backend failure

---

### Get Analysis History

Retrieve analysis history for a specific culture.

**Endpoint**: `GET /api/analyses?cultureId={cultureId}`

**Authentication**: Required (JWT Bearer token)

**Query Parameters**:
- `cultureId` (required): ID of the culture

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "cultureId": 1,
    "status": "healthy",
    "confidence": 0.85,
    "observations": "Clear, consistent bubbles...",
    "advice": "Continue current fermentation...",
    "timestamp": "2026-05-31T10:35:00Z"
  }
]
```

---

## Health & Monitoring Endpoints

### Health Check

Check backend service health.

**Endpoint**: `GET /actuator/health`

**Response** (200 OK):
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

---

### Metrics

Access application metrics.

**Endpoint**: `GET /actuator/metrics`

**Response** (200 OK):
```json
{
  "names": [
    "jvm.memory.used",
    "jvm.memory.committed",
    "http.server.requests",
    "process.cpu.usage"
  ]
}
```

---

### Application Info

Get application version and metadata.

**Endpoint**: `GET /actuator/info`

**Response** (200 OK):
```json
{
  "app": {
    "name": "Bio-State Fermentation Monitor",
    "description": "AI-powered fermentation health monitoring",
    "version": "0.0.1"
  }
}
```

---

## Error Handling

All error responses follow this format:

```json
{
  "timestamp": "2026-05-31T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error description"
}
```

### Common HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 400 | Bad Request - Invalid input or validation failed |
| 401 | Unauthorized - Missing or invalid authentication |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error - Server error occurred |

---

## Rate Limiting & Quotas

- **Gemini API**: Limited by your API quota; ensure sufficient credits
- **Image size**: Maximum 5MB per request
- **Concurrent requests**: No hard limit; limited by server resources

---

## Examples

### Complete Workflow

1. **Register**:
   ```bash
   curl -X POST http://localhost:8080/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"user1","email":"user@example.com","password":"Pass1234!"}'
   ```

2. **Login**:
   ```bash
   curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"user1","password":"Pass1234!"}'
   ```

3. **Create Culture**:
   ```bash
   curl -X POST http://localhost:8080/api/cultures \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{"name":"My Sourdough","type":"sourdough"}'
   ```

4. **Analyze Image**:
   ```bash
   curl -X POST http://localhost:8080/api/fermentation/analyze \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{"base64Image":"data:image/jpeg;base64,...","cultureType":"sourdough","cultureId":1}'
   ```
