package com.company.wms.putaway.repository;

import com.company.wms.putaway.domain.PutAwayTask;
import com.company.wms.putaway.domain.PutAwayTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PutAwayTaskRepository extends JpaRepository<PutAwayTask, UUID> {

    Optional<PutAwayTask> findByTaskNumber(String taskNumber);

    boolean existsByGoodsReceiptLineId(String goodsReceiptLineId);

    Optional<PutAwayTask> findByGoodsReceiptLineId(String goodsReceiptLineId);

    Page<PutAwayTask> findByStatus(PutAwayTaskStatus status, Pageable pageable);

    Page<PutAwayTask> findByAssignedTo(String assignedTo, Pageable pageable);

    List<PutAwayTask> findByStatusAndAssignedTo(PutAwayTaskStatus status, String assignedTo);

    @Query("""
           select t from PutAwayTask t
           where t.status = :status
             and t.assignedTo is null
           order by t.createdAt asc
           """)
    List<PutAwayTask> findUnassignedByStatus(@Param("status") PutAwayTaskStatus status,
                                             Pageable pageable);

    long countByStatus(PutAwayTaskStatus status);
}