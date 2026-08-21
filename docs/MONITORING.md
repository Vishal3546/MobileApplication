# Production Monitoring

## API Latency
Target P95 latency < 200ms for standard queries.

## Error Rates
Alert on 5xx rates > 1% in a 5-minute rolling window.
Alert on 401 rates > 5% indicating possible credential stuffing or mass token expiration bugs.

## Infrastructure Health
- PostgreSQL: Monitor Active Connections, CPU utilization, and free storage space.
- Redis: Monitor memory usage and evictions to prevent rate limiter failure or token loss.
- AWS S3: Monitor upload/download 5xx errors and bucket size growth.

## Security Event Monitoring
Use `AuditService` log aggregates to monitor for:
- Repeated unauthorized (403) attempts on cross-branch data.
- Privilege escalation attempts.
- Repeated payment conflicts or validation failures.

## Business KPI Alerts
Set alerts for unusual business anomalies:
- Purchase Failures Spike.
- Sales Failures Spike.
- Payment Failures Spike.
- Inventory Reservation Conflicts Spike.
- KYC Rejection Spikes (indicative of broken OCR or malicious bots).

## Observability
Trace requests using structured JSON logging across the stack. Ensure JVM memory and CPU are graphed for container health.
