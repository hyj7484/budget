package com.app.application.budget.record;

import java.time.OffsetDateTime;

public record PeriodRecord(
    OffsetDateTime from, 
    OffsetDateTime to
) {}