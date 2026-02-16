package com.app.application.budget.record;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryStatRecord(
    UUID categoryId, 
    String name, 
    String icon, 
    BigDecimal amount
) {}
