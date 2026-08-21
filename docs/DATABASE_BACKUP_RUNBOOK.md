# Database Backup Runbook

## Strategy & Frequency
- **Frequency**: Automated full `pg_dump` executed daily at 03:00 UTC.
- **Retention**: Immutable snapshots retained for 30 days in AWS S3 Standard-IA, followed by Glacier transition for 7 years (compliance constraint).
- **Encryption**: KMS-encrypted at rest in the S3 bucket.

## Staging Restore Drill
A Staging Restore Drill verifies the integrity of the backup data and the ability to spin up an environment rapidly.

**Procedure**:
1. Download the latest encrypted backup payload from the S3 backup bucket:
   `aws s3 cp s3://mobilebiz-db-backups/latest-production-backup.sql.gz ./restore/`
2. Decrypt and decompress:
   `gunzip ./restore/latest-production-backup.sql.gz`
3. Load into isolated Staging Database:
   `psql -h <staging-host> -U postgres -d staging_db -f ./restore/latest-production-backup.sql`
4. Spin up Staging Backend mapping to the `staging_db`.
5. Connect Angular Staging to the Backend Staging.
6. **Integrity Check**:
   - Verify `GET /api/v1/users` returns correct users.
   - Verify `GET /api/v1/inventory` matches the previous day's EOD values.
7. Report drill success.

*Note: Drill results MUST be recorded in the security audit log to prove compliance with disaster recovery SLAs.*
