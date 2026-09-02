package com.ahmedali.fulfillops.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ahmedali.fulfillops.payment.domain.QualityInspectionStatus;
import org.junit.jupiter.api.Test;

class QualityInspectionPolicyTest {
  private final QualityInspectionPolicy policy = new QualityInspectionPolicy();

  @Test
  void passesAnOrderWithoutARejectedSku() {
    QualityInspectionPolicy.Decision decision = policy.inspect("WIDGET-001,WIDGET-002");

    assertThat(decision.status()).isEqualTo(QualityInspectionStatus.PASSED);
    assertThat(decision.reasonCode()).isNull();
  }

  @Test
  void failsAnOrderContainingARejectedSku() {
    QualityInspectionPolicy.Decision decision = policy.inspect("WIDGET-001,QC-FAIL-DENTED");

    assertThat(decision.status()).isEqualTo(QualityInspectionStatus.FAILED);
    assertThat(decision.reasonCode()).isEqualTo("QUALITY_RULE_REJECTED");
  }
}
