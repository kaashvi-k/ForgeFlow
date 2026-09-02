-- Add the quality workflow state and quality-failure cancellation reason without rewriting history.
ALTER TABLE orders DROP CONSTRAINT orders_status_check;
ALTER TABLE orders ADD CONSTRAINT orders_status_check
    CHECK (status IN (
        'PENDING', 'INVENTORY_RESERVED', 'QUALITY_PASSED', 'PAYMENT_AUTHORIZED',
        'FULFILLMENT_ASSIGNED', 'PICKING', 'PACKED', 'DISPATCHED',
        'DELIVERED', 'CANCELLATION_PENDING', 'CANCELLED', 'REQUIRES_REVIEW'
    ));

ALTER TABLE order_status_history DROP CONSTRAINT order_status_history_status_check;
ALTER TABLE order_status_history ADD CONSTRAINT order_status_history_status_check
    CHECK (status IN (
        'PENDING', 'INVENTORY_RESERVED', 'QUALITY_PASSED', 'PAYMENT_AUTHORIZED',
        'FULFILLMENT_ASSIGNED', 'PICKING', 'PACKED', 'DISPATCHED',
        'DELIVERED', 'CANCELLATION_PENDING', 'CANCELLED', 'REQUIRES_REVIEW'
    ));

ALTER TABLE order_cancellation DROP CONSTRAINT order_cancellation_cancellation_reason_code_check;
ALTER TABLE order_cancellation ADD CONSTRAINT order_cancellation_cancellation_reason_code_check
    CHECK (cancellation_reason_code IN (
        'QUALITY_FAILED', 'PAYMENT_DECLINED', 'FULFILLMENT_CANCELLED',
        'CUSTOMER_REQUESTED', 'OPERATOR_REQUESTED'
    ));