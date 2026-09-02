package com.ahmedali.fulfillops.payment.service;

import com.ahmedali.fulfillops.payment.domain.OrderQualityContext;
import com.ahmedali.fulfillops.payment.domain.OrderQualityContextRepository;
import com.ahmedali.fulfillops.payment.domain.QualityInspection;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class QualityInspectionService {
  private final OrderQualityContextRepository contextRepository;
  private final QualityInspectionRepository inspectionRepository;
  private final QualityInspectionPolicy policy;
  private final QualityInspectionTransaction transaction;

  public QualityInspectionService(
      OrderQualityContextRepository contextRepository,
      QualityInspectionRepository inspectionRepository,
      QualityInspectionPolicy policy,
      QualityInspectionTransaction transaction) {
    this.contextRepository = contextRepository;
    this.inspectionRepository = inspectionRepository;
    this.policy = policy;
    this.transaction = transaction;
  }

  public QualityInspection inspect(UUID orderId, UUID correlationId, UUID causationId) {
    Optional<QualityInspection> existing = inspectionRepository.findByOrderId(orderId);
    if (existing.isPresent()) {
      return existing.get();
    }
    OrderQualityContext context =
        contextRepository.findById(orderId).orElseThrow(() -> new OrderQualityContextNotYetAvailableException(orderId));
    return transaction.record(context, policy.inspect(context.getSkuProfile()), correlationId, causationId);
  }
}
