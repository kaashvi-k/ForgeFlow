package com.ahmedali.fulfillops.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Local order projection containing only the SKU profile needed by the inspection policy. */
@Entity
@Table(name = "order_quality_context")
public class OrderQualityContext {
  @Id private UUID orderId;
  private UUID customerId;
  private String skuProfile;
  private UUID correlationId;
  private Instant createdAt;

  protected OrderQualityContext() {}

  public OrderQualityContext(UUID orderId, UUID customerId, String skuProfile, UUID correlationId) {
    this.orderId = orderId;
    this.customerId = customerId;
    this.skuProfile = skuProfile;
    this.correlationId = correlationId;
    this.createdAt = Instant.now();
  }

  public UUID getOrderId() { return orderId; }
  public UUID getCustomerId() { return customerId; }
  public String getSkuProfile() { return skuProfile; }
}
