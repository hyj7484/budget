package com.app.application.budget.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditEventDto {
    
    /**
     * 이벤트 ID
     */
    private UUID id;

    /**
     * 원장 ID
     */
    private UUID ledgerId;

    /**
     * 수행자 ID
     */
    private UUID actorUserId;

    /**
     * 액션
     */
    private String action;

    /**
     * 엔티티 타입
     */
    private String entityType;

    /**
     * 엔티티 ID
     */
    private UUID entityId;

    /**
     * 필드명
     */
    private String fieldName;

    /**
     * 변경 전 값
     */
    private String oldValue;

    /**
     * 변경 후 값
     */
    private String newValue;

    /**
     * 생성일시
     */
    private OffsetDateTime createdAt;
}