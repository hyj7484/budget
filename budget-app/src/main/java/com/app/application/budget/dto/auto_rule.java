package com.app.application.budget.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.app.application.budget.domain.enums.RuleField;
import com.app.application.budget.domain.enums.RuleOperator;
import com.app.application.budget.domain.enums.TxStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class auto_rule {

    /**
     * 룰ID
     */
    private UUID id;

    /**
     * 원장 ID
     */
    private UUID ledgerId;

    /**
     * 룰명
     */
    private String name;

    /**
     * 우선순위
     */
    private Integer priority;

    /**
     * 사용 여부
     */
    private Boolean enabled;

    /**
     * 매칭 필드 (MERCHANT/MEMO)
     */
    private RuleField matchField;

    /**
     * 매칭 연산자 (CONTAINS/EQUALS/STARTS_WITH/ENDS_WITH/REGEX)
     */
    private RuleOperator matchOperator;

    /**
     * 매칭 값
     */
    private String matchValue;

    /**
     * 결제수단조건
     */
    private UUID paymentMethodId;

    /**
     * 최소값
     */
    private BigDecimal minAmount;

    /**
     * 최대값
     */
    private BigDecimal maxAmount;

    /**
     * 설정 카테고리
     */
    private UUID setCategoryId;

    /**
     * 설정 상태
     */
    private TxStatus setStatus;

    /**
     * 반복거래 표시 여부
     */
    private Boolean markRecurring;

    /**
     * 생성일시
     */
    private OffsetDateTime createdAt;

    /**
     * 수정일시
     */
    private OffsetDateTime updatedAt;

}
