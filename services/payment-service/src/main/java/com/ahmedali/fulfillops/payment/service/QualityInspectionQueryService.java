package com.ahmedali.fulfillops.payment.service;

import com.ahmedali.fulfillops.payment.domain.QualityInspection;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionHistory;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionHistoryRepository;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class QualityInspectionQueryService {
  private final QualityInspectionRepository inspectionRepository;
  private final QualityInspectionHistoryRepository historyRepository;

  public QualityInspectionQueryService(
      QualityInspectionRepository inspectionRepository,
      QualityInspectionHistoryRepository historyRepository) {
    this.inspectionRepository = inspectionRepository;
    this.historyRepository = historyRepository;
  }

  public Optional<QualityInspection> findInspection(UUID inspectionId) { return inspectionRepository.findById(inspectionId); }
  public List<QualityInspectionHistory> findHistory(UUID inspectionId) { return historyRepository.findByInspectionIdOrderByOccurredAtAsc(inspectionId); }
}
