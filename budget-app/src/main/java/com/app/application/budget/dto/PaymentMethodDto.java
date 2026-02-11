package com.app.application.budget.dto;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.app.application.budget.domain.enums.PaymentMethodType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentMethodDto {

    /**
     * 결제수단 ID
     */
    private UUID id;
    
    /**
     * 원장 ID
     */
    private UUID ledgerId;

    /**
     * 결제수단 유형 (CARD/CASH/BANK/TRANSIT)
     */
    private PaymentMethodType type;

    /**
     * 결제수단 이름
     */
    private String name;

    /**
     * 금융기관 이름
     */
    private String institution;

    /**
     * 카드 마지막 4자리
     */
    private String last4;

    /**
     * 활성화 여부
     */
    private Boolean isActive;

    /**
     * 생성일 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime createdAt;
    
    /**
     * 수정일시 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime updatedAt;
}
