package com.ahmedali.fulfillops.payment.web.dto;

import java.time.Instant;
import java.util.UUID;

public record QualityInspectionHistoryResponse(
    UUID historyId, String eventType, String detail, Instant occurredAt) {}
