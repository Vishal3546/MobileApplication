# Production Go-Live Checklist

## 1. Environment Separations
- [ ] **STAGING**: CI/CD configured to deploy to staging on `main` branch push. [REQUIRES PRODUCTION ENVIRONMENT]
- [ ] **PRODUCTION**: Secrets externalized and managed securely. [VERIFIED]

## 2. Database (PostgreSQL)
- [ ] SSL/TLS connection established. [REQUIRES PRODUCTION ENVIRONMENT]
- [ ] Strong credentials configured in `.env`. [VERIFIED]
- [ ] V1 -> Latest Flyway migration passes on clean DB. [VERIFIED]

## 3. Caching & Rate Limiting (Redis)
- [ ] Rate limits configured natively. [VERIFIED]
- [ ] Redis authentication enabled. [REQUIRES PRODUCTION ENVIRONMENT]

## 4. Object Storage (AWS S3)
- [ ] Private Bucket provisioned without public ACLs. [REQUIRES PRODUCTION ENVIRONMENT]
- [ ] IAM User configured with least privilege. [REQUIRES PRODUCTION ENVIRONMENT]

## 5. Backend (Spring Boot)
- [ ] `application-prod.properties` configured without hardcoded secrets. [VERIFIED]
- [ ] Security Config strictly enforces CORS (no `*`). [VERIFIED]

## 6. Frontend (Angular)
- [ ] Production build (`ng build`) generated without errors. [VERIFIED]
- [ ] No tokens or secrets leaked in `environment.prod.ts`. [VERIFIED]
- [ ] Route lazy-loading verified. [VERIFIED]

## 7. Mobile (Android)
- [ ] R8 Minification/Shrinking enabled (`isMinifyEnabled = true`). [VERIFIED]
- [ ] Cleartext traffic disabled (`usesCleartextTraffic="false"`). [VERIFIED]
- [ ] Build signed with release `.keystore` / `.jks`. [REQUIRES PRODUCTION ENVIRONMENT]
- [ ] `assembleRelease` generates AAB/APK artifacts correctly. [VERIFIED - LOCALLY]

## 8. Network & DNS
- [ ] Valid TLS certificates bound to `api.domain.com` and `admin.domain.com`. [REQUIRES PRODUCTION ENVIRONMENT]
- [ ] HTTP -> HTTPS redirects enforced. [REQUIRES PRODUCTION ENVIRONMENT]

## 9. Security & Auditing
- [ ] No log exposures (passwords, tokens, KYC IDs). [VERIFIED]
- [ ] CSP and HSTS Security Headers active. [VERIFIED]

## 10. Final Smoke Tests
- [ ] Backend integration suite passes. [VERIFIED]
- [ ] Login/Refresh token cycle functions appropriately without leaks. [VERIFIED]
