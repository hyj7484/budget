package com.app.application.budget.dashboard;

import com.app.application.budget.mapper.DashboardMapper;
import com.app.application.budget.mapper.LedgerMapper;
import com.app.application.budget.mapper.LedgerMemberMapper;
import com.app.application.budget.record.CategoryStatRecord;
import com.app.application.budget.record.LedgerMetaRecord;
import com.app.application.budget.record.PeriodRecord;
import com.app.application.budget.record.RecentTxRecord;
import com.app.application.budget.record.SummaryRecord;
import com.app.application.budget.dashboard.dto.DashboardResponse;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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

        LedgerMetaRecord meta = ledgerMapper.findMeta(ledgerId);
        ZoneId zone = ZoneId.of(meta.timezone());

       // 화면 출력할 기간 계산 (기본: 이번 달) 
        YearMonth target = (ym == null || ym.isBlank())
                ? YearMonth.now(zone)
                : YearMonth.parse(ym.trim());

        
        int lim = (limit == null || limit <= 0) ? 20 : Math.min(limit, 100);

        OffsetDateTime from = target.atDay(1).atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime to = target.plusMonths(1).atDay(1).atStartOfDay(zone).toOffsetDateTime();

        SummaryRecord s = dashboardMapper.sumIncomeExpense(ledgerId, from, to);

        // 수입
        BigDecimal income = s.income();
        // 지출
        BigDecimal expense = s.expense();
        // 잔액
        BigDecimal net = income.subtract(expense);

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
        List<SummaryRecord> monthlySummary = getMonthlySummary(ledgerId, from.minusMonths(5), to);

        return new DashboardResponse(
                new PeriodRecord(from, to),
                new SummaryRecord(income, expense, net, s.incomeCount(), s.expenseCount()),
                recent,
                top,
                monthlySummary
        );
    }

    // 최근 6개월간 지출 추이 그래프용 데이터 조회 기능
    private List<SummaryRecord> getMonthlySummary(UUID ledgerId, OffsetDateTime from, OffsetDateTime to) {
        List<SummaryRecord> monthlyTrends = new ArrayList<>();
        for(int i=0; i<monthlyTrendMonths; i++) {
            OffsetDateTime monthFrom = from.minusMonths(i);
            OffsetDateTime monthTo = to.minusMonths(i);
            monthlyTrends.add(dashboardMapper.sumIncomeExpense(ledgerId, monthFrom, monthTo));
        }
        return monthlyTrends;
    }
}
