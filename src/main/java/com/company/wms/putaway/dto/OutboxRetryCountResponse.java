
package com.company.wms.putaway.dto;

import java.time.Instant;
import java.util.UUID;

public record OutboxRetryCountResponse(
        UUID id,
        String eventType,
        String topic,
        String status,
        int attempts,
        String lastError,
        Instant createdAt,
        Instant publishedAt
) {
}