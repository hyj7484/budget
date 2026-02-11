package com.app.application.budget.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.app.application.budget.domain.enums.CategoryKind;
import com.app.application.budget.domain.enums.CategoryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryDto {
    
    /**
     * 카테고리 ID
     */
    private UUID id;

    /**
     * 원장 ID
     */
    private UUID ledgerId;

    /**
     * 카테고리 유형 (EXPENSE/INCOME)
     */
    private CategoryKind kind;

    /**
     * 상위 카테고리 ID
     */
    private UUID parentId;

    /**
     * 카테고리 이름
     */
    private String name;

    /**
     * 아이콘
     */
    private String icon;

    /**
     * 키워드 목록
     */
    private String[] keywords;

    /**
     * 상태 (ACTIVE/HIDDEN/ARCHIVED)
     */
    private CategoryStatus status;

    /**
     * 정렬 순서
     */
    private Integer sortOrder;

    /**
     * 생성일 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime createdAt;

    /**
     * 수정일시 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime updatedAt;
}
