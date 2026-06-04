# Security Policy

Security guidelines and responsible disclosure policy for Bio-State Fermentation Monitor.

## Overview

We take security seriously. This document outlines our security practices, vulnerability reporting procedures, and guidelines for keeping the application secure.

## Supported Versions

| Version | Supported |
|---------|-----------|
| 0.0.1   | ✓ Current |

## Reporting Security Vulnerabilities

**Do not open public issues for security vulnerabilities.**

If you discover a security vulnerability, please email: **security@example.com**

Include the following information:
- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Any proof of concept or exploit code
- Your name and contact information (optional)

We will acknowledge receipt within 48 hours and provide updates on investigation progress.

### Responsible Disclosure Timeline

- **Day 1**: Vulnerability report received and confirmed
- **Day 3**: Initial assessment completed
- **Day 7**: Patch development begins
- **Day 21**: Fix deployed to production (typical; may vary by severity)
- **Day 30**: Public disclosure and CVE assignment (if applicable)

## Security Best Practices

### For Users

1. **Keep Credentials Safe**
   - Never share passwords
   - Use strong, unique passwords
   - Enable MFA on your account (when available)

2. **API Key Management**
   - Store GEMINI_API_KEY securely (environment variables, not code)
   - Rotate keys regularly
   - Use separate keys per environment
   - Revoke compromised keys immediately

3. **Data Privacy**
   - Be aware that images sent for analysis may be stored
   - Don't send sensitive/private images
   - Review privacy policy before use

4. **Network Security**
   - Always use HTTPS in production
   - Don't access over public/untrusted networks
   - Use VPN for remote access

### For Developers

1. **Code Security**
   - Never commit secrets or API keys
   - Use `.env.example` for template; `.env` for local secrets
   - Add secrets to `.gitignore`
   - Review code for hardcoded credentials

2. **Dependency Management**
   ```bash
   # Frontend: audit for vulnerabilities
   npm audit
   npm audit fix

   # Backend: check for vulnerable dependencies
   mvn dependency-check:check
   ```

3. **Input Validation**
   - Validate all user inputs on frontend and backend
   - Use allowlists where possible (deny by default)
   - Sanitize data before database/API use
   - Implement rate limiting for API endpoints

4. **Authentication & Authorization**
   - Enforce strong passwords (8+ chars, mixed case, special chars)
   - Use JWT with secure expiration times
   - Verify tokens on protected routes
   - Log authentication failures
   - Implement account lockout after failed attempts

5. **Data Protection**
   - Hash passwords with BCrypt (or better)
   - Encrypt sensitive data at rest
   - Use HTTPS for all data in transit
   - Implement CORS restrictions
   - Use secure session cookies (HttpOnly, Secure, SameSite)

6. **Error Handling**
   - Don't leak sensitive information in error messages
   - Log detailed errors server-side only
   - Return generic errors to clients
   - Monitor for suspicious error patterns

### For Operators

1. **Access Control**
   - Limit database access to application servers only
   - Use secrets management (AWS Secrets Manager, Vault)
   - Implement principle of least privilege
   - Use VPC for network isolation

2. **Monitoring & Logging**
   - Enable audit logging for all changes
   - Monitor for suspicious API activity
   - Alert on authentication failures
   - Maintain log retention for compliance

3. **Infrastructure Security**
   - Keep OS and libraries patched
   - Use firewall rules to restrict traffic
   - Enable DDoS protection
   - Use load balancers with WAF
   - Regular penetration testing

4. **Backup & Disaster Recovery**
   - Regular automated backups
   - Test backup restoration
   - Maintain off-site copies
   - Document recovery procedures

## Security Headers

The backend implements the following security headers:

```
Content-Security-Policy: default-src 'self'
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

## Authentication & Authorization

### JWT Implementation

- Algorithm: HS256 (HMAC SHA-256)
- Token expiration: 24 hours (configurable)
- Secret key: Stored in environment variables
- Refresh tokens: Not currently implemented (planned)

### Password Policy

- Minimum length: 8 characters
- Required: Uppercase, lowercase, digit, special character
- Hashing: BCrypt with salt
- No password history

## API Security

### CORS Policy

- Development: Restricted to `http://localhost:5173`
- Production: Configure via environment variables
- Methods: GET, POST, PUT, DELETE as needed
- Headers: Authorization, Content-Type

### Rate Limiting

Currently not implemented. Recommended additions:
- Per-user rate limits (e.g., 100 requests/hour)
- Per-IP rate limits
- Endpoint-specific limits (e.g., /auth/login: 5 attempts/15 minutes)
- Exponential backoff for image processing

### Input Validation

| Field | Validation |
|-------|-----------|
| Username | 3-30 alphanumeric + underscore |
| Email | RFC 5322 format |
| Password | 8+ chars, upper, lower, digit, special |
| Image | Base64, JPEG/PNG/WebP, < 5MB |

## Third-Party Services

### Google Gemini API

- Endpoint: https://api.generativeai.google.com
- Authentication: API key in Authorization header
- Data: Images sent for analysis (review Google's privacy policy)
- Logging: Requests logged for audit trail

## Compliance

### Data Protection

- GDPR: Compliant with user data handling requirements
- CCPA: Supports user data export/deletion (to implement)
- Data retention: Define policy (currently no auto-deletion)

### Standards

- OWASP Top 10: Addressed in architecture
- CWE Top 25: Common weaknesses mitigated
- NIST Cybersecurity Framework: Principles followed

## Known Security Limitations

1. **H2 In-Memory Database**: Data lost on restart (not for production)
   - **Mitigation**: Use managed PostgreSQL/MySQL in production

2. **No Rate Limiting**: APIs vulnerable to brute force/DoS
   - **Mitigation**: Add rate limiting middleware

3. **No Refresh Tokens**: JWT tokens valid for 24 hours
   - **Mitigation**: Implement refresh token rotation

4. **No Multi-Factor Authentication (MFA)**: Single factor authentication only
   - **Mitigation**: Add TOTP/email-based MFA

5. **Limited Audit Logging**: Basic error logging only
   - **Mitigation**: Implement comprehensive audit trail

## Security Roadmap

- [ ] Implement rate limiting (Redis-based)
- [ ] Add refresh token mechanism
- [ ] Enable multi-factor authentication (MFA)
- [ ] Implement comprehensive audit logging
- [ ] Use managed database instead of H2
- [ ] Add IP whitelisting for admin endpoints
- [ ] Implement API key rotation schedules
- [ ] Add Web Application Firewall (WAF)
- [ ] Conduct regular penetration testing
- [ ] Implement SIEM integration

## Incident Response

### Suspected Breach

1. Immediately disable affected API keys
2. Notify security team
3. Investigate scope and impact
4. Review logs for unauthorized access
5. Notify affected users if data exposed
6. Implement remediation
7. Document lessons learned

### Security Incident Contact

Email: **security@example.com**

## References

- [OWASP Top 10](https://owasp.org/Top10/)
- [CWE/SANS Top 25](https://cwe.mitre.org/top25/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [OWASP JWT Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html)

## Acknowledgments

We appreciate security researchers who responsibly disclose vulnerabilities. We recognize contributors to our security and privacy efforts.

---

**Last Updated**: 2026-06-04

For security inquiries, contact: security@example.com
