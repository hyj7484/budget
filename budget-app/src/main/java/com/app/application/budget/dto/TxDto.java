package com.app.application.budget.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.app.application.budget.domain.enums.TxStatus;
import com.app.application.budget.domain.enums.TxType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TxDto {
    
    /**
     * 거래 내역 ID
     */
    private UUID id;

    /**
     * 원장 ID
     */
    private UUID ledgerId;

    /**
     * 발생일시 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime occurredAt;

    /**
     * 거래 내역 유형 (EXPENSE/INCOME/TRANSFER)
     */
    private TxType type;

    /**
     * 거래 내역 상태 (UNCATEGORIZED/CATEGORIZED/RECURRING/CONFIRMED/VOID)
     */
    private TxStatus status;

    /**
     * 통화 코드 (USD/KRW/JPY 등)
     */
    private String currencyCode;       

    /**
     * 금액
     */
    private BigDecimal amount;          

    /**
     * 상점명
     */
    private String merchant;

    /**
     * 메모
     */
    private String memo;

    /**
     * 즐겨찾기 여부
     */
    private Boolean isFavorite;

    /**
     * 인식 신뢰도 (0~100)
     */
    private Short confidence;           

    /**
     * 결제수단 ID
     */
    private UUID paymentMethodId;

    /**
     * 카테고리 ID
     */
    private UUID categoryId;

    /**
     * 출금 결제수단 ID
     */
    private UUID fromPaymentMethodId;

    /**
     * 입금 결제수단 ID
     */
    private UUID toPaymentMethodId;

    /**
     * 생성일 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime createdAt;

    /**
     * 수정일시 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime updatedAt;

    /**
     * 삭제일시 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime deletedAt;
}
