package com.app.application.budget.record;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RecentTxRow(
    UUID id,
    String type,
    String status,
    OffsetDateTime occurredAt,
    BigDecimal amount,
    String currencyCode,
    UUID categoryId,
    String categoryName,
    String categoryIcon,
    UUID paymentMethodId,
    String paymentMethodName,
    UUID toPaymentMethodId,
    String toPaymentMethodName,
    String merchant,
    String memo
) {}