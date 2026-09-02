package com.ahmedali.fulfillops.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

/** One immutable, terminal inspection decision per order. */
@Entity
@Table(name = "quality_inspections")
public class QualityInspection {
  @Id private UUID inspectionId;
  private UUID orderId;
  private UUID customerId;
  @Enumerated(EnumType.STRING) private QualityInspectionStatus status;
  private String reasonCode;
  private String reasonDetail;
  private UUID correlationId;
  @Version private long version;
  private Instant createdAt;
  private Instant updatedAt;

  protected QualityInspection() {}

  public QualityInspection(
      UUID inspectionId,
      UUID orderId,
      UUID customerId,
      QualityInspectionStatus status,
      String reasonCode,
      String reasonDetail,
      UUID correlationId) {
    this.inspectionId = inspectionId;
    this.orderId = orderId;
    this.customerId = customerId;
    this.status = status;
    this.reasonCode = reasonCode;
    this.reasonDetail = reasonDetail;
    this.correlationId = correlationId;
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  public UUID getInspectionId() { return inspectionId; }
  public UUID getOrderId() { return orderId; }
  public UUID getCustomerId() { return customerId; }
  public QualityInspectionStatus getStatus() { return status; }
  public String getReasonCode() { return reasonCode; }
  public String getReasonDetail() { return reasonDetail; }
  public UUID getCorrelationId() { return correlationId; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
}
