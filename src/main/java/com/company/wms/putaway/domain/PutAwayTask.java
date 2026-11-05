package com.company.wms.putaway.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "putaway_task", uniqueConstraints = {
		@UniqueConstraint(name = "uk_putaway_task_number", columnNames = "task_number"),
		@UniqueConstraint(name = "uk_putaway_task_grl", columnNames = "goods_receipt_line_id") })
public class PutAwayTask {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	@Column(name = "task_number", nullable = false, length = 64)
	private String taskNumber;
	@Column(name = "goods_receipt_line_id", nullable = false, length = 64)
	private String goodsReceiptLineId;
	@Column(name = "sku_id", nullable = false, length = 64)
	private String skuId;
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	@Column(name = "target_bin_id", nullable = false, length = 64)
	private String targetBinId;
	@Column(name = "warehouse_id", nullable = false, length = 64)
	private String warehouseId;
	
	@Column(name = "correlation_id", length = 100)
	private String correlationId;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 32)
	private PutAwayTaskStatus status;
	@Column(name = "assigned_to", length = 128)
	private String assignedTo;
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	@Column(name = "completed_at")
	private Instant completedAt;
	@Version
	@Column(name = "version", nullable = false)
	private Long version;

	public PutAwayTask() {
	}

	public PutAwayTask(UUID id, String taskNumber, String goodsReceiptLineId, String skuId, Integer quantity,
			String targetBinId, String warehouseId, PutAwayTaskStatus status, String assignedTo, Instant createdAt,
			Instant completedAt, Long version) {
		this.id = id;
		this.taskNumber = taskNumber;
		this.goodsReceiptLineId = goodsReceiptLineId;
		this.skuId = skuId;
		this.quantity = quantity;
		this.targetBinId = targetBinId;
		this.warehouseId = warehouseId;
		this.status = status;
		this.assignedTo = assignedTo;
		this.createdAt = createdAt;
		this.completedAt = completedAt;
		this.version = version;
	}

	@PrePersist
	void onCreate() {
		if (this.createdAt == null) {
			this.createdAt = Instant.now();
		}
		if (this.status == null) {
			this.status = PutAwayTaskStatus.CREATED;
		}
	}

	public void assignTo(String user) {
		if (this.status == PutAwayTaskStatus.COMPLETED) {
			throw new IllegalStateException("Cannot assign a completed task");
		}
		if (this.status == PutAwayTaskStatus.CANCELLED) {
			throw new IllegalStateException("Cannot assign a cancelled task");
		}
		this.assignedTo = user;
		this.status = PutAwayTaskStatus.ASSIGNED;
	}

	public void start() {
		if (this.status != PutAwayTaskStatus.ASSIGNED && this.status != PutAwayTaskStatus.CREATED) {
			throw new IllegalStateException("Task cannot be started from status " + this.status);
		}
		this.status = PutAwayTaskStatus.IN_PROGRESS;
	}

	public void complete() {
		if (this.status == PutAwayTaskStatus.COMPLETED) {
			throw new IllegalStateException("Task already completed");
		}
		if (this.status == PutAwayTaskStatus.CANCELLED) {
			throw new IllegalStateException("Cancelled task cannot be completed");
		}
		this.status = PutAwayTaskStatus.COMPLETED;
		this.completedAt = Instant.now();
	}

	public void cancel() {
		if (this.status == PutAwayTaskStatus.COMPLETED) {
			throw new IllegalStateException("Completed task cannot be cancelled");
		}
		this.status = PutAwayTaskStatus.CANCELLED;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(String taskNumber) {
		this.taskNumber = taskNumber;
	}

	public String getGoodsReceiptLineId() {
		return goodsReceiptLineId;
	}

	public void setGoodsReceiptLineId(String goodsReceiptLineId) {
		this.goodsReceiptLineId = goodsReceiptLineId;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getTargetBinId() {
		return targetBinId;
	}

	public void setTargetBinId(String targetBinId) {
		this.targetBinId = targetBinId;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public PutAwayTaskStatus getStatus() {
		return status;
	}

	public void setStatus(PutAwayTaskStatus status) {
		this.status = status;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Instant completedAt) {
		this.completedAt = completedAt;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	public String getCorrelationId() {
	    return correlationId;
	}

	public void setCorrelationId(String correlationId) {
	    this.correlationId = correlationId;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private UUID id;
		private String taskNumber;
		private String goodsReceiptLineId;
		private String skuId;
		private Integer quantity;
		private String targetBinId;
		private String warehouseId;
		private String correlationId;
		private PutAwayTaskStatus status;
		private String assignedTo;
		private Instant createdAt;
		private Instant completedAt;
		private Long version;

		public Builder id(UUID id) {
			this.id = id;
			return this;
		}

		public Builder taskNumber(String taskNumber) {
			this.taskNumber = taskNumber;
			return this;
		}

		public Builder goodsReceiptLineId(String goodsReceiptLineId) {
			this.goodsReceiptLineId = goodsReceiptLineId;
			return this;
		}

		public Builder skuId(String skuId) {
			this.skuId = skuId;
			return this;
		}

		public Builder quantity(Integer quantity) {
			this.quantity = quantity;
			return this;
		}

		public Builder targetBinId(String targetBinId) {
			this.targetBinId = targetBinId;
			return this;
		}

		public Builder warehouseId(String warehouseId) {
			this.warehouseId = warehouseId;
			return this;
		}

		public Builder status(PutAwayTaskStatus status) {
			this.status = status;
			return this;
		}
		public Builder correlationId(String correlationId) {
		    this.correlationId = correlationId;
		    return this;
		}

		public Builder assignedTo(String assignedTo) {
			this.assignedTo = assignedTo;
			return this;
		}

		public Builder createdAt(Instant createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Builder completedAt(Instant completedAt) {
			this.completedAt = completedAt;
			return this;
		}

		public Builder version(Long version) {
			this.version = version;
			return this;
		}

		public PutAwayTask build() {
			return new PutAwayTask(id, taskNumber, goodsReceiptLineId, skuId, quantity, targetBinId, warehouseId,
					status, assignedTo, createdAt, completedAt, version);
		}
	}

	@Override
	public String toString() {
		return "PutAwayTask{" + "id=" + id + ", taskNumber='" + taskNumber + '\'' + ", goodsReceiptLineId='"
				+ goodsReceiptLineId + '\'' + ", skuId='" + skuId + '\'' + ", quantity=" + quantity + ", targetBinId='"
				+ targetBinId + '\'' + ", warehouseId='" + warehouseId + '\'' + ", status=" + status + ", assignedTo='"
				+ assignedTo + '\'' + ", createdAt=" + createdAt + ", completedAt=" + completedAt + ", version="
				+ version + '}';
	}
}