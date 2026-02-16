package com.app.application.budget.record;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryStatRow(
    UUID categoryId, 
    String name, 
    String icon, 
    BigDecimal amount
) {}
