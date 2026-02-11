package com.app.application.budget.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.app.application.budget.domain.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LedgerMemberDto {
    /**
     * 원장 ID
     */
    private UUID ledgerId;

    /**
     * 사용자 ID
     */
    private UUID userId;
    
    /**
     * 권한 (OWNER/EDITOR/VIEWER)
     */
    private UserRole role;

    /**
     * 생성일 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime createdAt;
}
