# System Rollback Runbook

## 1. Backend Rollback (Spring Boot)
- **Strategy**: Revert to the previous stable Docker image / JAR artifact.
- **Action**: `docker stop mobilebiz-backend && docker run -d --name mobilebiz-backend mobilebiz/backend:previous-tag`
- **Validation**: Check `/actuator/health` to ensure `UP` status.

## 2. Database Migrations (PostgreSQL / Flyway)
- **Strategy**: Because Flyway migrations are strictly forward-moving in this configuration, a database schema rollback requires restoring from the pre-deployment snapshot (assuming destructive DDL was introduced).
- **Action**:
  1. Stop application traffic.
  2. Restore from AWS S3 backup: `psql -h <host> -U postgres -f pre_deploy_snapshot.sql`
  3. Restart application.
- **Validation**: Verify that the schema is aligned with the reverted application code version.

## 3. Frontend Rollback (Angular)
- **Strategy**: Revert static assets on CDN/Web Server.
- **Action**: Repoint load balancer/CDN to the previous `dist/` directory or revert to the previous container.
- **Validation**: Verify SPA loads and API connectivity succeeds with no console errors.

## 4. Mobile Rollback (Android)
- **Strategy**: A rolled out Android application via Play Store cannot be easily "uninstalled" from user devices.
- **Action**: 
  - Immediate fix: Deploy a hotfix release using an incremented `versionCode` compiled from the previous stable branch.
  - Mitigate: Ensure backward compatibility in APIs so older app versions continue to function.
- **Validation**: Verify the hotfix APK installs cleanly over the broken release APK.

## 5. Configuration Rollback
- **Strategy**: Restore previous secrets or environmental configurations.
- **Action**: Revert changes in `.env` or AWS Secrets Manager. Trigger a rolling restart of backend instances to pick up old credentials.
