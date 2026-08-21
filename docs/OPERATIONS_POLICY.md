# Operations & Governance Policy

## 1. Data Retention
- **KYC & PII**: Immutable. Soft-deletion is implemented; absolute hard deletion requires a legal compliance request (GDPR Right to be Forgotten) triggering a manual masking process.
- **Transactions & Payments**: Retained for 7 years minimum to comply with financial auditing standards.
- **Audit Logs**: Immutable records of every state change (User creation, Branch mutation, Sale completion). Cannot be modified by Super Admins.
- **Media Assets**: Encrypted at rest. Deleted automatically if the parent KYC record is wiped.

## 2. Release Versioning (Semantic)
- **Backend (Spring Boot)**: `vMAJOR.MINOR.PATCH` embedded in `application.properties` and Docker tags.
- **Frontend (Angular)**: Mirrors the backend versioning exactly.
- **Mobile (Android)**: 
  - `versionName`: Follows semantic `MAJOR.MINOR.PATCH` (e.g. `1.0.0`).
  - `versionCode`: Strictly monotonic integer. **Never reuse a versionCode**; Play Store will reject the binary.

## 3. CI/CD Governance
- Direct commits to `main` are restricted.
- Deployments require passing GitHub Actions (Backend Unit/Integration Tests, Angular Prod Build, Android Release Assemble).
- Approvals are required before merging into `main`.

## 4. Dependency Maintenance
- Review dependencies monthly.
- Security patches (e.g. CVE alerts from Dependabot/Snyk) must be applied immediately.
- Major version upgrades (e.g. Angular 17 -> 18, Spring Boot 3.3 -> 3.4) require dedicated regression phases and must pass all E2E integration tests natively before merging.
- **Do not blindly upgrade major dependencies.**
