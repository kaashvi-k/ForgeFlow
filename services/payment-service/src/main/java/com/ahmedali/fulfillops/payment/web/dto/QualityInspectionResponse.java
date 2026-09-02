package com.ahmedali.fulfillops.payment.web.dto;

import java.time.Instant;
import java.util.UUID;

public record QualityInspectionResponse(
    UUID inspectionId,
    UUID orderId,
    UUID customerId,
    String status,
    String reasonCode,
    String reasonDetail,
    Instant createdAt,
    Instant updatedAt) {}
