# Deployment Guide

Production deployment guide for Bio-State Fermentation Monitor.

## Prerequisites

- Docker and Docker Compose (for containerized deployment)
- Java 25+ and Maven 3.6+ (for native deployment)
- Node.js 20+ (for frontend)
- A valid Google Gemini API key
- SSL/TLS certificates for HTTPS (recommended)
- Environment variables configured securely

## Environment Variables

Before deploying, set these environment variables:

```bash
export GEMINI_API_KEY="your_production_api_key"
export BACKEND_PORT=8080
export FRONTEND_PORT=5173
export SPRING_PROFILES_ACTIVE=prod
```

**Critical Security Notes**:
- Never commit API keys to source control
- Use a secrets management system (AWS Secrets Manager, HashiCorp Vault, etc.)
- Rotate API keys regularly
- Use different keys for development, staging, and production

## Option 1: Docker Compose (Recommended)

### Build and Start

1. Prepare environment file:
   ```bash
   cp .env.example .env
   # Edit .env with production values
   ```

2. Build images:
   ```bash
   docker-compose build
   ```

3. Start services:
   ```bash
   docker-compose up -d
   ```

4. Verify health:
   ```bash
   curl http://localhost:8080/actuator/health
   curl http://localhost:5173/
   ```

### Production Configuration

For production, update `docker-compose.yml`:

```yaml
version: '3.8'
services:
  backend:
    image: bio-state-monitor-backend:latest
    restart: always
    ports:
      - "8080:8080"
    environment:
      - GEMINI_API_KEY=${GEMINI_API_KEY}
      - SPRING_PROFILES_ACTIVE=prod
      - JAVA_OPTS="-Xmx1024m -Xms512m"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 40s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  frontend:
    image: bio-state-monitor-frontend:latest
    restart: always
    ports:
      - "5173:5173"
    environment:
      - VITE_BACKEND_URL=http://backend:8080
    depends_on:
      backend:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:5173/"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 30s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### Scaling

For high availability:

1. Use a reverse proxy (Nginx, HAProxy)
2. Run multiple backend instances with load balancing
3. Use external database (PostgreSQL) instead of H2
4. Implement caching layer (Redis)

## Option 2: Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (1.20+)
- kubectl configured
- Docker images pushed to registry

### Deploy

1. Create namespace:
   ```bash
   kubectl create namespace bio-state-monitor
   ```

2. Create secrets:
   ```bash
   kubectl create secret generic api-keys \
     --from-literal=GEMINI_API_KEY=your_api_key \
     -n bio-state-monitor
   ```

3. Apply manifests:
   ```bash
   kubectl apply -f k8s/ -n bio-state-monitor
   ```

4. Verify deployment:
   ```bash
   kubectl get pods -n bio-state-monitor
   kubectl logs -f deployment/backend -n bio-state-monitor
   ```

## Option 3: Cloud Platform Deployment

### Azure App Service

1. Create resource group:
   ```bash
   az group create --name bio-state-rg --location eastus
   ```

2. Deploy backend:
   ```bash
   az webapp create --resource-group bio-state-rg \
     --plan bio-state-plan --name bio-state-backend \
     --runtime "java|25"
   ```

3. Deploy frontend:
   ```bash
   az webapp create --resource-group bio-state-rg \
     --plan bio-state-plan --name bio-state-frontend \
     --runtime "node|20"
   ```

### AWS Elastic Container Service (ECS)

1. Create ECS cluster
2. Push Docker images to ECR
3. Create task definitions
4. Deploy services
5. Configure load balancer

### Google Cloud Run

1. Build and push images:
   ```bash
   gcloud builds submit --tag gcr.io/PROJECT/bio-state-backend
   gcloud builds submit --tag gcr.io/PROJECT/bio-state-frontend
   ```

2. Deploy:
   ```bash
   gcloud run deploy bio-state-backend --image gcr.io/PROJECT/bio-state-backend
   gcloud run deploy bio-state-frontend --image gcr.io/PROJECT/bio-state-frontend
   ```

## Security Best Practices

### 1. SSL/TLS Encryption

- Use HTTPS in production
- Obtain certificates from Let's Encrypt or your CA
- Configure Nginx/HAProxy as reverse proxy

Example Nginx config:
```nginx
server {
  listen 443 ssl http2;
  server_name your-domain.com;

  ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
  ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

  location / {
    proxy_pass http://backend:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }
}
```

### 2. API Key Rotation

- Rotate Gemini API keys every 90 days
- Store keys in secrets management system
- Use separate keys per environment

### 3. Database Security

- Use managed PostgreSQL/MySQL for production (not H2)
- Enable encryption at rest
- Use strong database passwords
- Restrict database access to backend only
- Enable audit logging

### 4. Network Security

- Use VPC/network isolation
- Restrict traffic to necessary ports only
- Enable WAF (Web Application Firewall)
- Use private load balancers
- Implement rate limiting

### 5. Monitoring & Logging

- Enable CloudWatch/Stackdriver logs
- Monitor error rates and performance
- Set up alerts for anomalies
- Retain logs for audit trail (90+ days)
- Use centralized logging (ELK, Splunk)

## Performance Optimization

### Backend

```properties
# application.properties for production
server.tomcat.threads.max=200
server.tomcat.accept-count=100
spring.datasource.hikari.maximum-pool-size=20
logging.level.root=WARN
```

### Frontend

- Enable gzip compression
- Use CDN for static assets
- Implement service workers for caching
- Optimize images and lazy load components

### Database

- Use connection pooling
- Create indices on frequently queried columns
- Archive old analysis data
- Monitor query performance

## Backup & Recovery

### Database Backup

```bash
# PostgreSQL backup
pg_dump -h db-host -U user database > backup.sql

# Restore
psql -h db-host -U user database < backup.sql
```

### Docker Volume Backup

```bash
docker run --rm -v bio-state-db:/data \
  -v $(pwd):/backup ubuntu \
  tar czf /backup/db-backup.tar.gz -C /data .
```

### Disaster Recovery

- Maintain regular backups (daily)
- Test recovery procedures monthly
- Document recovery runbooks
- Maintain off-site backup copies

## Monitoring & Alerting

### Health Checks

```bash
# Backend health
curl http://backend:8080/actuator/health

# Metrics
curl http://backend:8080/actuator/metrics
```

### Alerting Rules

- CPU usage > 80% for 5 minutes
- Memory usage > 85%
- API error rate > 1% for 10 minutes
- Response time > 2 seconds average
- Database connection pool near capacity

### Logging

```properties
logging.level.com.biostate.monitor=INFO
logging.level.org.springframework.web=WARN
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

## Troubleshooting

### Backend won't start

```bash
# Check logs
docker logs bio-state-monitor-backend

# Verify environment variables
docker exec bio-state-monitor-backend env | grep GEMINI
```

### Frontend can't reach backend

```bash
# Verify backend is running
curl http://localhost:8080/actuator/health

# Check CORS configuration
# Check VITE_BACKEND_URL environment variable
```

### Performance issues

```bash
# Check system resources
docker stats

# Check database performance
# Monitor Gemini API response times
# Check network latency
```

## Rollback Procedure

1. Keep previous Docker image tags
2. Update docker-compose.yml to previous image
3. Restart services: `docker-compose up -d`
4. Verify functionality
5. Document what went wrong

## Compliance & Auditing

- Enable API access logging
- Track user actions for audit trail
- Implement role-based access control (RBAC)
- Document all deployments with timestamps
- Regular security audits
- GDPR compliance for user data

## Support & Maintenance

- Keep dependencies updated
- Monitor security advisories
- Regular penetration testing
- Document runbooks
- Train team on operational procedures
