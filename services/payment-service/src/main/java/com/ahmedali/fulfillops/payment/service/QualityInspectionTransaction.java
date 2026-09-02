package com.ahmedali.fulfillops.payment.service;

import com.ahmedali.fulfillops.payment.domain.OrderQualityContext;
import com.ahmedali.fulfillops.payment.domain.QualityInspection;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionHistory;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionHistoryRepository;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionRepository;
import com.ahmedali.fulfillops.payment.messaging.OutboxEventWriter;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Persists the decision, its history entry, and the corresponding outbox event atomically. */
@Component
public class QualityInspectionTransaction {
  private final QualityInspectionRepository inspectionRepository;
  private final QualityInspectionHistoryRepository historyRepository;
  private final OutboxEventWriter outboxEventWriter;

  public QualityInspectionTransaction(
      QualityInspectionRepository inspectionRepository,
      QualityInspectionHistoryRepository historyRepository,
      OutboxEventWriter outboxEventWriter) {
    this.inspectionRepository = inspectionRepository;
    this.historyRepository = historyRepository;
    this.outboxEventWriter = outboxEventWriter;
  }

  @Transactional
  public QualityInspection record(
      OrderQualityContext context,
      QualityInspectionPolicy.Decision decision,
      UUID correlationId,
      UUID causationId) {
    QualityInspection inspection =
        new QualityInspection(
            UUID.randomUUID(),
            context.getOrderId(),
            context.getCustomerId(),
            decision.status(),
            decision.reasonCode(),
            decision.reasonDetail(),
            correlationId);
    inspectionRepository.save(inspection);
    String eventType = decision.status().name().equals("PASSED") ? "QualityPassed" : "QualityFailed";
    historyRepository.save(
        new QualityInspectionHistory(
            inspection.getInspectionId(), context.getOrderId(), eventType, decision.reasonDetail()));
    outboxEventWriter.write(
        eventType,
        1,
        context.getOrderId(),
        correlationId,
        causationId,
        new QualityPayload(
            inspection.getInspectionId(), decision.reasonCode(), decision.reasonDetail()));
    return inspection;
  }

  private record QualityPayload(UUID inspectionId, String reasonCode, String reasonDetail) {}
}
