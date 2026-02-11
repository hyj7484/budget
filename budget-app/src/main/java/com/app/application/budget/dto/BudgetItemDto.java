package com.app.application.budget.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BudgetItemDto {

    /**
     * 예산항목 ID
     */
    private UUID id;

    /**
     * 예산 ID
     */
    private UUID budgetId;

    /**
     * 카테고리 ID
     */
    private UUID categoryId;

    /**
     * 예산금액
     */
    private BigDecimal amount;
    
    /**
     * 알림 임계치 퍼센트
     */
    private Short alertThresholdPercent;

    /**
     * 고정비 여부
     */
    private Boolean isFixedCost;

    /**
     * 생성일시
     */
    private OffsetDateTime createdAt;

    /**
     * 수정일시
     */
    private OffsetDateTime updatedAt;
}
