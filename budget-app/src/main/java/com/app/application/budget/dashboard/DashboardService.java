package com.app.application.budget.dashboard;

import com.app.application.budget.mapper.DashboardMapper;
import com.app.application.budget.mapper.LedgerMapper;
import com.app.application.budget.mapper.LedgerMemberMapper;
import com.app.application.budget.record.CategoryStatRecord;
import com.app.application.budget.record.LedgerMetaRecord;
import com.app.application.budget.record.MonthlySummaryRecord;
import com.app.application.budget.record.PeriodRecord;
import com.app.application.budget.record.RecentTxRecord;
import com.app.application.budget.record.SummaryRecord;
import com.app.application.budget.dashboard.dto.DashboardResponse;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LedgerMemberMapper ledgerMemberMapper;
    private final LedgerMapper ledgerMapper;
    private final DashboardMapper dashboardMapper;

    private final int TopDefaultLimit = 5;
    private final int monthlyTrendMonths = 6;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UUID userId, UUID ledgerId, String ym, Integer limit) {

        if (!ledgerMemberMapper.existsMember(ledgerId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a ledger member");
        }

        // 원장 메타 정보 조회 (시간대 정보 필요)
        LedgerMetaRecord meta = ledgerMapper.findMeta(ledgerId);
        ZoneId zone = ZoneId.of(meta.timezone());

       // 화면 출력할 기간 계산 (기본: 이번 달) 
        YearMonth target = (ym == null || ym.isBlank())
                ? YearMonth.now(zone)
                : YearMonth.parse(ym.trim());

        
        int lim = (limit == null || limit <= 0) ? 20 : Math.min(limit, 100);

        OffsetDateTime from = target.atDay(1).atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime to = target.plusMonths(1).atDay(1).atStartOfDay(zone).toOffsetDateTime();

        // 수입/지출 합계 및 순액 조회
        SummaryRecord summaryRecord = dashboardMapper.sumIncomeExpense(ledgerId, from, to);
        
        // 최근 거래 내역 조회 
        List<RecentTxRecord> recentRows = dashboardMapper.selectRecent(ledgerId, lim);
        List<RecentTxRecord> recent = recentRows.stream()
                .map(r -> new RecentTxRecord(
                        r.id(), r.type(), r.status(), r.occurredAt(),
                        r.amount(), r.currencyCode(),
                        r.categoryId(), r.categoryName(), r.categoryIcon(),
                        r.paymentMethodId(), r.paymentMethodName(),
                        r.toPaymentMethodId(), r.toPaymentMethodName(),
                        r.merchant(), r.memo()
                ))
                .toList();

        // 지출 상위 Top5 카테고리 조회
        List<CategoryStatRecord> top = dashboardMapper.selectTopExpenseCategories(ledgerId, from, to, TopDefaultLimit).stream()
                .map(r -> new CategoryStatRecord(r.categoryId(), r.name(), r.icon(), r.amount()))
                .toList();

         // 최근 6개월간 지출 추이 그래프용 데이터 조회 기능
        List<MonthlySummaryRecord> monthlySummary = getMonthlySummary(ledgerId, from.minusMonths(monthlyTrendMonths-1), to);

        return new DashboardResponse(
                new PeriodRecord(from, to),
                summaryRecord,
                recent,
                top,
                monthlySummary
        );
    }

    // 최근 6개월간 지출 추이 그래프용 데이터 조회 기능
    private List<MonthlySummaryRecord> getMonthlySummary(UUID ledgerId, OffsetDateTime from, OffsetDateTime to) {
        List<MonthlySummaryRecord> monthlyTrends = new ArrayList<>();
        for(int i=0; i<monthlyTrendMonths; i++) {
            OffsetDateTime monthFrom = from.plusMonths(i);
            OffsetDateTime monthTo = monthFrom.plusMonths(1);
            SummaryRecord monthlySummaryRecord = dashboardMapper.sumIncomeExpense(ledgerId, monthFrom, monthTo);
            monthlyTrends.add(new MonthlySummaryRecord(
                    monthFrom.toLocalDate().toString().substring(0,7), // "YYYY-MM"
                    monthlySummaryRecord.income(),
                    monthlySummaryRecord.expense(),
                    monthlySummaryRecord.net(),
                    monthlySummaryRecord.incomeCount(),
                    monthlySummaryRecord.expenseCount()
            ));
        }
        return monthlyTrends;
    }

    public DashboardResponse getRecentByType(UUID userId, UUID ledgerId, Integer type) {

        return null;
    }
}
