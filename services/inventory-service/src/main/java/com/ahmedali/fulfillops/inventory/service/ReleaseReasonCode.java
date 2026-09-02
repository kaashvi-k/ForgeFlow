package com.ahmedali.fulfillops.inventory.service;

/** Matches InventoryReleased.v1's closed reasonCode enum in contracts/events/. */
public enum ReleaseReasonCode {
  QUALITY_FAILED,
  PAYMENT_DECLINED,
  FULFILLMENT_CANCELLED,
  ORDER_CANCELLED
}
