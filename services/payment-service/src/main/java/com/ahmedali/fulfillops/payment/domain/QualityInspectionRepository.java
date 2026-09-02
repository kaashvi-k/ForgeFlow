package com.ahmedali.fulfillops.payment.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityInspectionRepository extends JpaRepository<QualityInspection, UUID> {
  Optional<QualityInspection> findByOrderId(UUID orderId);
}
