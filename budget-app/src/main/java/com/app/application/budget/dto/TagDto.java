package com.app.application.budget.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TagDto {
    /**
     * 태그 ID
     */
    private UUID id;

    /**
     * 원장 ID
     */
    private UUID ledgerId;
    
    /**
     * 태그 이름
     */
    private String name;

    /**
     * 생성일 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime createdAt;
}
