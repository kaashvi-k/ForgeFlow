package com.ahmedali.fulfillops.payment.service;

import java.util.UUID;

public class QualityInspectionNotFoundException extends RuntimeException {
  public QualityInspectionNotFoundException(UUID inspectionId) {
    super("quality inspection " + inspectionId + " was not found");
  }
}
