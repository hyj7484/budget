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
public class TxLineDto {
    
    /**
     * 분할ID
     */
    private UUID id;

    /**
     * 거래 ID
     */
    private UUID txId;

    /** 
     * 카테고리 ID
     */
    private UUID categoryId;

    /**
     * 금액
     */
    private BigDecimal amount;

    /**
     * 메모
     */
    private String memo;

    /**
     * 정렬 순서
     */
    private Integer sortOrder;

    /** 
     * 생성일 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime createdAt;
}
