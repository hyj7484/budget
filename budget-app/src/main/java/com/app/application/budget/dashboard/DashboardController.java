package com.app.application.budget.dashboard;

import com.app.application.budget.dashboard.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ledgers")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{ledgerId}/dashboard")
    public DashboardResponse dashboard(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID ledgerId,
            @RequestParam(required = false) String ym,
            @RequestParam(required = false) Integer limit
    ) {
        return dashboardService.getDashboard(userId, ledgerId, ym, limit);
    }
}
