package com.app.application.budget.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImportBatchDto {
    /**
     * 배치 ID
     */
    private UUID id;

    /**
     * 원장 ID
     */
    private UUID ledgerId;

    /**
     * 소스
     */
    private String source;

    /**
     * 파일명
     */
    private String fileName;

    /**
     * 상태 (RUNNING/DONE/FAILED)
     */
    private String status;

    /**
     * 생성자 ID
     */
    private UUID createdBy;

    /**
     * 생성일시
     */
    private OffsetDateTime createdAt;
}