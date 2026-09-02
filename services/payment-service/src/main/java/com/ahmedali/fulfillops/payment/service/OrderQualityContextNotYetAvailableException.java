package com.ahmedali.fulfillops.payment.service;

import java.util.UUID;

/** Retriable because the prerequisite OrderPlaced event may arrive on another topic later. */
public class OrderQualityContextNotYetAvailableException extends RuntimeException {
  public OrderQualityContextNotYetAvailableException(UUID orderId) {
    super("quality context for order " + orderId + " is not available yet");
  }
}
