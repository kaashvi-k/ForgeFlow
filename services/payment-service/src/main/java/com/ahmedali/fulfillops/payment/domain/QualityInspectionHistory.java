package com.ahmedali.fulfillops.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Append-only audit history for an inspection decision. */
@Entity
@Table(name = "quality_inspection_history")
public class QualityInspectionHistory {
  @Id private UUID historyId;
  private UUID inspectionId;
  private UUID orderId;
  private String eventType;
  private String detail;
  private Instant occurredAt;

  protected QualityInspectionHistory() {}

  public QualityInspectionHistory(UUID inspectionId, UUID orderId, String eventType, String detail) {
    this.historyId = UUID.randomUUID();
    this.inspectionId = inspectionId;
    this.orderId = orderId;
    this.eventType = eventType;
    this.detail = detail;
    this.occurredAt = Instant.now();
  }

  public UUID getHistoryId() { return historyId; }
  public UUID getInspectionId() { return inspectionId; }
  public UUID getOrderId() { return orderId; }
  public String getEventType() { return eventType; }
  public String getDetail() { return detail; }
  public Instant getOccurredAt() { return occurredAt; }
}
