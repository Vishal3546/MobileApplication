# Backup & Recovery Runbook

## PostgreSQL
- **Strategy**: Daily pg_dumpall piped to AWS S3 encrypted bucket.
- **Retention**: 30 days.
- **Recovery Procedure**:
  1. Fetch latest backup from S3: `aws s3 cp s3://db-backups/latest.sql.gz .`
  2. Unzip: `gunzip latest.sql.gz`
  3. Restore: `psql -h <host> -U postgres -f latest.sql`

## Redis
- **Strategy**: Redis RDB snapshots saved every 60 seconds if 1000 keys changed, or strictly relying on ephemeral cache states.
- **Recovery Procedure**: Typically ephemeral. Refresh tokens will be lost, forcing all users to log in again upon catastrophic Redis loss.
