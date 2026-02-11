package com.app.application.budget.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.app.application.budget.domain.enums.NotifyFreq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSettingDto {

    /**
     * 사용자 설정 ID
     */
    private UUID userId;

    /**
     * 기본 원장 ID
     */
    private UUID defaultLedgerId;

    /**
     * 지역화 설정 (ko-KR, en-US 등)
     */
    private String locale;

    /**
     * 시간대 (Asia/Seoul, America/New_York 등)
     */
    private String timezone;

    /**
     * 통화 코드 (KRW, USD 등)
     */
    private String currencyCode;

    /**
     * 다크 모드 여부
     */
    private Boolean darkMode;

    /**
     * 알림 설정
     */
    private Boolean notifyBudget;

    /**
     * 알림 빈도 (OFF/DAILY/WEEKLY/MONTHLY)
     */
    private NotifyFreq notifyUncategorized;

    /**
     * 반복감지알림
     */
    private Boolean notifyRecurringDetect;

    /**
     * 수정일시
     */
    private OffsetDateTime updatedAt;
}