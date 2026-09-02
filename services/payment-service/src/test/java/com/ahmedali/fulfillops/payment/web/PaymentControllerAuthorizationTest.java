package com.ahmedali.fulfillops.payment.web;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ahmedali.fulfillops.payment.config.SecurityConfig;
import com.ahmedali.fulfillops.payment.config.TestSecurityConfig;
import com.ahmedali.fulfillops.payment.domain.QualityInspection;
import com.ahmedali.fulfillops.payment.domain.QualityInspectionStatus;
import com.ahmedali.fulfillops.payment.service.QualityInspectionQueryService;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class, GlobalExceptionHandler.class})
class PaymentControllerAuthorizationTest {
  @Autowired private MockMvc mockMvc;
  @MockitoBean private QualityInspectionQueryService queryService;

  @Test
  void operatorCanReadQualityInspection() throws Exception {
    UUID inspectionId = UUID.randomUUID();
    when(queryService.findInspection(inspectionId))
        .thenReturn(
            Optional.of(
                new QualityInspection(
                    inspectionId,
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    QualityInspectionStatus.PASSED,
                    null,
                    null,
                    UUID.randomUUID())));

    mockMvc
        .perform(
            get("/api/v1/quality-inspections/{inspectionId}", inspectionId)
                .with(
                    jwt()
                        .jwt(token -> token.claim("realm_access", Map.of("roles", List.of("OPERATOR"))))
                        .authorities(SecurityConfig::realmRolesAsAuthorities)))
        .andExpect(status().isOk());
  }

  @Test
  void customerCannotReadQualityInspection() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/quality-inspections/{inspectionId}", UUID.randomUUID())
                .with(
                    jwt()
                        .jwt(token -> token.claim("realm_access", Map.of("roles", List.of("CUSTOMER"))))
                        .authorities(SecurityConfig::realmRolesAsAuthorities)))
        .andExpect(status().isForbidden());
  }
}
