package com.app.application.budget.dashboard.dto;

import java.util.List;

import com.app.application.budget.record.CategoryStatRecord;
import com.app.application.budget.record.MonthlySummaryRecord;
import com.app.application.budget.record.PeriodRecord;
import com.app.application.budget.record.RecentTxRecord;
import com.app.application.budget.record.SummaryRecord;

public record DashboardResponse(
    // 표시 기간 정보
    PeriodRecord period,
    // 수입/지출 합계 및 순액
    SummaryRecord summary,
    // 최근 거래 내역 (최대 limit 건)
    List<RecentTxRecord> recent,
    // 지출 상위 카테고리 (최대 5개)
    List<CategoryStatRecord> topExpenseCategories,
    // 최근 6개월 간 월별 수입/지출 추이 (최대 6개월)
    List<MonthlySummaryRecord> monthlyTrends
    ) {
}
