package com.app.application.budget.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TxAttachmentDto {
    /**
     * 첨부파일 ID
     */
    private UUID id;

    /**
     * 거래 ID
     */
    private UUID txId;

    /**
     * 파일 이름
     */
    private String fileName;

    /**
     * MIME 타입
     */
    private String mimeType;

    /** 
     * 파일 크기 (바이트 단위)
     */
    private Long fileSize;

    /**
     * 저장 URL
     */
    private String storageUrl;

    /**
     * 생성일 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime createdAt;
}
