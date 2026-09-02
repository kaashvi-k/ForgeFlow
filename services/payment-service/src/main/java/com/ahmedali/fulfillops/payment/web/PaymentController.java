package com.ahmedali.fulfillops.payment.web;

import com.ahmedali.fulfillops.payment.domain.QualityInspection;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionHistory;
import com.ahmedali.fulfillops.payment.service.QualityInspectionNotFoundException;
import com.ahmedali.fulfillops.payment.service.QualityInspectionQueryService;
import com.ahmedali.fulfillops.payment.web.dto.QualityInspectionHistoryResponse;
import com.ahmedali.fulfillops.payment.web.dto.QualityInspectionResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read API for the durable quality-inspection decision and its append-only history. */
@RestController
@RequestMapping("/api/v1/quality-inspections")
public class PaymentController {
  private final QualityInspectionQueryService queryService;

  public PaymentController(QualityInspectionQueryService queryService) {
    this.queryService = queryService;
  }

  @GetMapping("/{inspectionId}")
  public QualityInspectionResponse getInspection(@PathVariable UUID inspectionId) {
    QualityInspection inspection =
        queryService
            .findInspection(inspectionId)
            .orElseThrow(() -> new QualityInspectionNotFoundException(inspectionId));
    return toResponse(inspection);
  }

  @GetMapping("/{inspectionId}/history")
  public List<QualityInspectionHistoryResponse> getHistory(@PathVariable UUID inspectionId) {
    getInspection(inspectionId);
    return queryService.findHistory(inspectionId).stream().map(PaymentController::toResponse).toList();
  }

  private static QualityInspectionResponse toResponse(QualityInspection inspection) {
    return new QualityInspectionResponse(
        inspection.getInspectionId(),
        inspection.getOrderId(),
        inspection.getCustomerId(),
        inspection.getStatus().name(),
        inspection.getReasonCode(),
        inspection.getReasonDetail(),
        inspection.getCreatedAt(),
        inspection.getUpdatedAt());
  }

  private static QualityInspectionHistoryResponse toResponse(QualityInspectionHistory history) {
    return new QualityInspectionHistoryResponse(
        history.getHistoryId(), history.getEventType(), history.getDetail(), history.getOccurredAt());
  }
}
