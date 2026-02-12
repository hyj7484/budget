package com.app.application.budget.dashboard.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record DashboardResponse(
        Period period,
        Summary summary,
        List<RecentTx> recent,
        List<CategoryStat> topExpenseCategories
) {
    public record Period(OffsetDateTime from, OffsetDateTime to) {}

    public record Summary(
            BigDecimal income,
            BigDecimal expense,
            BigDecimal net,
            long incomeCount,
            long expenseCount
    ) {}

    public record RecentTx(
            UUID id,
            String type,              // EXPENSE/INCOME/TRANSFER
            String status,            // POSTED/DRAFT
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

    public record CategoryStat(
            UUID categoryId,
            String name,
            String icon,
            BigDecimal amount
    ) {}
}
