
package com.company.wms.putaway.repository;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.company.wms.putaway.outbox.OutboxEvent;
import com.company.wms.putaway.outbox.OutboxStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    // Existing method preserved for compatibility.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select o from OutboxEvent o
            where o.status = :status
            order by o.createdAt asc
            """)
    List<OutboxEvent> findBatchForPublishing(
            @Param("status") OutboxStatus status,
            Pageable pageable);

    // New method: selects only events whose retry time has arrived.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select o from OutboxEvent o
            where o.status = :status
              and (
                    o.nextAttemptAt is null
                    or o.nextAttemptAt <= :now
              )
            order by o.createdAt asc
            """)
    List<OutboxEvent> findReadyForPublishing(
            @Param("status") OutboxStatus status,
            @Param("now") Instant now,
            Pageable pageable);

    // Existing method preserved.
    long countByStatus(OutboxStatus status);

    // Existing method preserved for manual retry operations.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select o from OutboxEvent o
            where o.id = :id
            """)
    Optional<OutboxEvent> findByIdForUpdate(
            @Param("id") UUID id);
}
