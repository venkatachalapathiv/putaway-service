CREATE INDEX ix_putaway_status          ON putaway_task(status);
CREATE INDEX ix_putaway_assigned_to     ON putaway_task(assigned_to);
CREATE INDEX ix_putaway_status_assigned ON putaway_task(status, assigned_to);

CREATE INDEX ix_outbox_pending
  ON outbox_event(status, created_at)
  WHERE status = 'PENDING';

CREATE INDEX ix_processed_event_type ON processed_event(event_type);