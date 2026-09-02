package com.ahmedali.fulfillops.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ahmedali.fulfillops.payment.domain.OrderQualityContextRepository;
import com.ahmedali.fulfillops.payment.domain.QualityInspection;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionRepository;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class QualityInspectionServiceTest {
  private final OrderQualityContextRepository contextRepository = mock(OrderQualityContextRepository.class);
  private final QualityInspectionRepository inspectionRepository = mock(QualityInspectionRepository.class);
  private final QualityInspectionTransaction transaction = mock(QualityInspectionTransaction.class);
  private final QualityInspectionService service =
      new QualityInspectionService(
          contextRepository, inspectionRepository, new QualityInspectionPolicy(), transaction);

  @Test
  void duplicateTriggerReturnsTheOriginalInspectionWithoutCreatingAnotherDecision() {
    UUID orderId = UUID.randomUUID();
    QualityInspection existing =
        new QualityInspection(
            UUID.randomUUID(),
            orderId,
            UUID.randomUUID(),
            QualityInspectionStatus.PASSED,
            null,
            null,
            UUID.randomUUID());
    when(inspectionRepository.findByOrderId(orderId)).thenReturn(Optional.of(existing));

    QualityInspection returned = service.inspect(orderId, UUID.randomUUID(), UUID.randomUUID());

    assertThat(returned).isSameAs(existing);
    verifyNoInteractions(contextRepository, transaction);
  }
}
