package com.ahmedali.fulfillops.payment.service;

import com.ahmedali.fulfillops.payment.domain.QualityInspectionStatus;
import org.springframework.stereotype.Component;

/**
 * Deterministic inspection policy for this demonstrator. A SKU beginning with {@code QC-FAIL-}
 * represents a known failed quality check; all other SKU profiles pass. The input comes from the
 * OrderPlaced event, not from another service database.
 */
@Component
public class QualityInspectionPolicy {
  public Decision inspect(String skuProfile) {
    if (skuProfile.contains("QC-FAIL-")) {
      return new Decision(
          QualityInspectionStatus.FAILED,
          "QUALITY_RULE_REJECTED",
          "One or more requested SKUs are marked as failing the deterministic quality rule.");
    }
    return new Decision(QualityInspectionStatus.PASSED, null, null);
  }

  public record Decision(QualityInspectionStatus status, String reasonCode, String reasonDetail) {}
}
