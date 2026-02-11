package com.app.application.budget.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LedgerDto {
    /**
     * 원장 ID
     */
    private UUID id;

    /**
     * 소유자 사용자 ID
     */
    private UUID ownerUserId;

    /**
     * 원장 이름
     */
    private String name;

    /**
     * 기본 통화
     */
    private String baseCurrency;

    /**
     * 타임존 (Asia/Seoul, America/New_York 등)
     */
    private String timezone;

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
