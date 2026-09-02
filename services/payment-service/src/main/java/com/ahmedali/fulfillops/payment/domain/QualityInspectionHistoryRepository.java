package com.ahmedali.fulfillops.payment.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityInspectionHistoryRepository
    extends JpaRepository<QualityInspectionHistory, UUID> {
  List<QualityInspectionHistory> findByInspectionIdOrderByOccurredAtAsc(UUID inspectionId);
}
