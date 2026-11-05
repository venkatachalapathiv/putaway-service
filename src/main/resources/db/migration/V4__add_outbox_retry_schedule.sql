
ALTER TABLE outbox_event
    ADD COLUMN next_attempt_at TIMESTAMPTZ;

UPDATE outbox_event
SET next_attempt_at = created_at
WHERE status = 'PENDING';

CREATE INDEX ix_outbox_retry_schedule
    ON outbox_event (status, next_attempt_at, created_at);
