package com.ahmedali.fulfillops.payment.messaging;

import com.ahmedali.fulfillops.payment.domain.OrderQualityContext;
import com.ahmedali.fulfillops.payment.domain.OrderQualityContextRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Payment Service's view of everything order-service reports on its own topic: OrderPlaced (build a
 * local order-context projection — see OrderPaymentContext) and OrderCancellationRequested (refund
 * whatever authorized payment this order still has — a no-op if there is none, e.g. cancellation
 * was requested before payment was ever authorized). Both event types share one consumer group on
 * this topic, since Kafka only delivers each message to one consumer per group — two separate
 * listener classes here would silently split the traffic between them instead of each seeing every
 * message.
 */
@Component
public class OrderEventsListener {

  private static final Logger log = LoggerFactory.getLogger(OrderEventsListener.class);
  private static final String CONSUMER_NAME = "quality-service.order-events";
  private static final String ORDER_PLACED_EVENT_TYPE = "OrderPlaced";

  private final InboxEventRepository inboxEventRepository;
  private final OrderQualityContextRepository orderQualityContextRepository;
  private final DeadLetterEventRecorder deadLetterEventRecorder;
  private final KafkaListenerMetrics metrics;
  private final ObjectMapper objectMapper;
  private final String topic;

  public OrderEventsListener(
      InboxEventRepository inboxEventRepository,
      OrderQualityContextRepository orderQualityContextRepository,
      DeadLetterEventRecorder deadLetterEventRecorder,
      KafkaListenerMetrics metrics,
      ObjectMapper objectMapper,
      @Value("${app.messaging.order-events-topic}") String topic) {
    this.inboxEventRepository = inboxEventRepository;
    this.orderQualityContextRepository = orderQualityContextRepository;
    this.deadLetterEventRecorder = deadLetterEventRecorder;
    this.metrics = metrics;
    this.objectMapper = objectMapper;
    this.topic = topic;
  }

  @RetryableTopic(
      attempts = "4",
      backOff = @BackOff(delay = 500, multiplier = 2.0, maxDelay = 5000),
      exclude = NonRetryableEventProcessingException.class)
  @KafkaListener(
      topics = "${app.messaging.order-events-topic}",
      groupId = "${spring.application.name}")
  @Transactional
  public void onMessage(String envelopeJson) {
    EventEnvelope envelope = objectMapper.readValue(envelopeJson, EventEnvelope.class);
    MDC.put("correlationId", envelope.correlationId().toString());
    MDC.put("eventId", envelope.eventId().toString());
    MDC.put("aggregateId", envelope.aggregateId().toString());
    try {
      InboxEventId id = new InboxEventId(envelope.eventId(), CONSUMER_NAME);
      if (inboxEventRepository.existsById(id)) {
        log.info(
            "duplicate delivery of {} for order {}, already processed, skipping",
            envelope.eventType(),
            envelope.aggregateId());
        metrics.recordDuplicate(envelope.eventType());
        return;
      }

      try {
        dispatch(envelope);
      } catch (RuntimeException processingFailure) {
        log.warn(
            "processing failed for {} on order {}, errorClass={}",
            envelope.eventType(),
            envelope.aggregateId(),
            processingFailure.getClass().getSimpleName());
        metrics.recordProcessingFailure(
            envelope.eventType(), processingFailure.getClass().getSimpleName());
        throw processingFailure;
      }

      inboxEventRepository.save(new InboxEvent(id, envelope.eventType(), envelope.aggregateId()));
      log.info("processed {} for order {}", envelope.eventType(), envelope.aggregateId());
    } finally {
      MDC.remove("correlationId");
      MDC.remove("eventId");
      MDC.remove("aggregateId");
    }
  }

  private void dispatch(EventEnvelope envelope) {
    switch (envelope.eventType()) {
      case ORDER_PLACED_EVENT_TYPE ->
          orderQualityContextRepository.save(parseContextOrFailNonRetryably(envelope));
      default ->
          log.debug(
              "ignoring unrecognized event type={} on the order events topic",
              envelope.eventType());
    }
  }

  @DltHandler
  public void onDlt(String envelopeJson) {
    EventEnvelope envelope = objectMapper.readValue(envelopeJson, EventEnvelope.class);
    deadLetterEventRecorder.record(envelope, CONSUMER_NAME, topic, envelopeJson);
    metrics.recordDeadLettered(envelope.eventType());
    log.error(
        "event routed to dead-letter topic after exhausting retries: type={} eventId={} orderId={}",
        envelope.eventType(),
        envelope.eventId(),
        envelope.aggregateId());
  }

  /**
   * A malformed payload (missing/mistyped fields) will fail identically on every redelivery, so
   * it's wrapped as non-retryable and routed straight to the DLT instead of wasting the retry
   * budget on something that can never succeed.
   */
  private static OrderQualityContext parseContextOrFailNonRetryably(EventEnvelope envelope) {
    try {
      JsonNode payload = envelope.payload();
      UUID customerId = UUID.fromString(payload.get("customerId").asString());
      JsonNode items = payload.get("items");
      if (items == null || !items.isArray() || items.isEmpty()) {
        throw new IllegalArgumentException("OrderPlaced items must be a non-empty array");
      }
      List<String> skus = new ArrayList<>();
      for (JsonNode item : items) {
        JsonNode sku = item.get("sku");
        if (sku == null || sku.asString().isBlank()) {
          throw new IllegalArgumentException("OrderPlaced item is missing sku");
        }
        skus.add(sku.asString());
      }
      return new OrderQualityContext(
          envelope.aggregateId(), customerId, String.join(",", skus), envelope.correlationId());
    } catch (RuntimeException malformed) {
      throw new NonRetryableEventProcessingException(
          "malformed OrderPlaced payload for event "
              + envelope.eventId()
              + ": "
              + malformed.getMessage());
    }
  }
}
