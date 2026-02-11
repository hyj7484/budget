package com.app.application.budget.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.app.application.budget.domain.enums.BudgetPeriodType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BudgetDto {
    /**
     * 예산 ID
     */
    private UUID id;

    /**
     * 원장 ID
     */
    private UUID ledgerId;

    /**
     * 기간타입
     */
    private BudgetPeriodType periodType;

    /**
     * 시작일
     */
    private LocalDate periodStart;

    /**
     * 종료일
     */
    private LocalDate periodEnd;

    /**
     * 이월여부
     */
    private Boolean rollover;

    /**
     * 생성일시
     */
    private OffsetDateTime createdAt;

    /**
     * 수정일시
     */
    private OffsetDateTime updatedAt;
}
