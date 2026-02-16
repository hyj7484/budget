package com.app.application.budget.record;

import java.math.BigDecimal;

public record SummaryRow(
    BigDecimal income,
    BigDecimal expense,
    Long incomeCount,
    Long expenseCount) {}
    