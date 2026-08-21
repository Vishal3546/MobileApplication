# Incident Response Runbook

## 1. Backend Outage
**Detect**: PagerDuty fires due to `/actuator/health` 503 or load balancer 5xx spikes.
**Contain**: Divert traffic to secondary region or scale-up healthy instances via auto-scaling groups.
**Recover**: If caused by OOM or deadlock, trigger rolling restarts. Identify bad code push and initiate Rollback Runbook.
**Verify**: Manual Smoke Test (Login, Purchase, Sale).
**Communicate**: Post internal Slack update.
**Postmortem**: Root Cause Analysis meeting within 48h.

## 2. PostgreSQL Outage
**Detect**: 500 errors regarding HikariPool timeouts.
**Contain**: Pause incoming writes via rate limiters if possible.
**Recover**: If RDS/CloudSQL failover hasn't kicked in, manually trigger replica promotion. If data corruption, refer to Database Backup Runbook for restore drill.
**Verify**: Check data integrity of latest transactions.

## 3. Redis Outage
**Detect**: Rate Limiters blocking all traffic, or users logging out en masse (due to dropped refresh tokens).
**Contain**: The application is configured to fail safely and force re-login if refresh tokens are lost. No data corruption will occur.
**Recover**: Reboot Redis instance. Memory will start fresh.
**Verify**: Perform Login and verify JWT issuance.

## 4. Auth & Security Breach
**Detect**: Mass 401 rate spikes, or unusual branch role elevations detected by AuditService.
**Contain**: Kill all active tokens by FLUSHALL on Redis (Refresh Tokens) and disable the compromised User ID.
**Recover**: Enforce password resets. Patch vulnerability.
**Verify**: Verify penetration tests against the hotfix.
**Communicate**: Notify impacted users per GDPR/CCPA requirements.

## 5. Media S3 Outage / Corruption
**Detect**: KYC uploads failing with 500 or Downloads returning 404s.
**Contain**: Halt onboarding workflows.
**Recover**: Check AWS IAM key rotation. If a bucket was deleted, recover from S3 versioning/MFA delete protection.
**Verify**: Perform a test KYC upload and download.
