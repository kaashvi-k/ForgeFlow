package com.ahmedali.fulfillops.payment.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderQualityContextRepository extends JpaRepository<OrderQualityContext, UUID> {}
