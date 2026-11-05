CREATE TABLE putaway_task (
  id                    UUID PRIMARY KEY,
  task_number           VARCHAR(64)  NOT NULL,
  goods_receipt_line_id VARCHAR(64)  NOT NULL,
  sku_id                VARCHAR(64)  NOT NULL,
  quantity              INT          NOT NULL CHECK (quantity > 0),
  target_bin_id         VARCHAR(64)  NOT NULL,
  status                VARCHAR(32)  NOT NULL,
  assigned_to           VARCHAR(128),
  created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
  completed_at          TIMESTAMPTZ,
  version               BIGINT       NOT NULL DEFAULT 0,
  CONSTRAINT uk_putaway_task_number UNIQUE (task_number),
  CONSTRAINT uk_putaway_task_grl    UNIQUE (goods_receipt_line_id)
);

CREATE TABLE outbox_event (
  id             UUID PRIMARY KEY,
  aggregate_type VARCHAR(64)  NOT NULL,
  aggregate_id   VARCHAR(64)  NOT NULL,
  event_type     VARCHAR(64)  NOT NULL,
  topic          VARCHAR(128) NOT NULL,
  event_key      VARCHAR(128),
  payload        JSONB        NOT NULL,
  status         VARCHAR(16)  NOT NULL,
  attempts       INT          NOT NULL DEFAULT 0,
  last_error     VARCHAR(1000),
  created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
  published_at   TIMESTAMPTZ
);

CREATE TABLE processed_event (
  event_id     VARCHAR(64) PRIMARY KEY,
  event_type   VARCHAR(64) NOT NULL,
  processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);