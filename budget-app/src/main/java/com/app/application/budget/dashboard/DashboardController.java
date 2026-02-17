package com.app.application.budget.dashboard;

import com.app.application.budget.dashboard.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드 API - 수입/지출 합계, 최근 거래 내역 등
     * @param userId    - 요청 헤더 "X-User-Id"에서 전달되는 사용자 ID (UUID)
     * @param ledgerId  - 조회할 원장 ID
     * @param ym    - Optional, 조회할 기간의 연월 (예: "2024-08"). 기본값은 현재 연월
     * @param limit - Optional, 최근 거래 내역 조회 시 최대 몇 건까지 반환할지 (기본: 20, 최대: 100)
     * @param paymentMethodType - Optional, 조회할 거래의 결제 수단 유형 (예: CARD, CASH 등)
     * @return
     */
    @GetMapping("/{ledgerId}")
    public DashboardResponse dashboard(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID ledgerId,
            @RequestParam(required = false) String ym,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String paymentMethodType
    ) {
        return dashboardService.getDashboard(userId, ledgerId, ym, limit, paymentMethodType);
    }
}
