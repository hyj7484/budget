package com.app.application.budget.dto;


import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUserDto {
    /**
     * 사용자 ID
     */
    private UUID id;

    /**
     * 이메일
     */
    private String email;

    /**
     * 비밀번호 해시
     */
    private String passwordHash;

    /**
     * 표시이름
     */
    private String displayName;

    /**
     * 로컬 (ko-KR, en-US 등)
     */
    private String locale;

    /**
     * 타임존 (Asia/Seoul, America/New_York 등)
     */
    private String timezone;

    /**
     * 기본 통화 (KRW, USD 등)
     */
    private String defaultCurrency;

    /**
     * 생성일 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime  createdAt;

    /**
     * 수정일시 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime  updatedAt;

    /**
     * 삭제일시 (YYYY-MM-DD HH24:MI:SS)
     */
    private OffsetDateTime deletedAt;
}
