package com.app.application.budget.record;

import java.math.BigDecimal;

public record MonthlySummaryRecord(
    String month,
    BigDecimal income,
    BigDecimal expense,
    BigDecimal net,
    Long incomeCount,
    Long expenseCount
) {}