package com.app.application.budget.dashboard;

import com.app.application.budget.mapper.DashboardMapper;
import com.app.application.budget.mapper.LedgerMapper;
import com.app.application.budget.mapper.LedgerMemberMapper;
import com.app.application.budget.record.LedgerMetaRow;
import com.app.application.budget.record.RecentTxRow;
import com.app.application.budget.record.SummaryRow;
import com.app.application.budget.dashboard.dto.DashboardResponse;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LedgerMemberMapper ledgerMemberMapper;
    private final LedgerMapper ledgerMapper;
    private final DashboardMapper dashboardMapper;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UUID userId, UUID ledgerId, String ym, Integer limit) {

        if (!ledgerMemberMapper.existsMember(ledgerId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a ledger member");
        }

        LedgerMetaRow meta = ledgerMapper.findMeta(ledgerId);
        ZoneId zone = ZoneId.of(meta.timezone());

       // 화면 출력할 기간 계산 (기본: 이번 달) 
        YearMonth target = (ym == null || ym.isBlank())
                ? YearMonth.now(zone)
                : YearMonth.parse(ym.trim());

        
        int lim = (limit == null || limit <= 0) ? 20 : Math.min(limit, 100);

        OffsetDateTime from = target.atDay(1).atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime to = target.plusMonths(1).atDay(1).atStartOfDay(zone).toOffsetDateTime();

        SummaryRow s = dashboardMapper.sumIncomeExpense(ledgerId, from, to);
        BigDecimal income = s.income();
        BigDecimal expense = s.expense();
        BigDecimal net = income.subtract(expense);

        List<RecentTxRow> recentRows = dashboardMapper.selectRecent(ledgerId, lim);
        List<DashboardResponse.RecentTx> recent = recentRows.stream()
                .map(r -> new DashboardResponse.RecentTx(
                        r.id(), r.type(), r.status(), r.occurredAt(),
                        r.amount(), r.currencyCode(),
                        r.categoryId(), r.categoryName(), r.categoryIcon(),
                        r.paymentMethodId(), r.paymentMethodName(),
                        r.toPaymentMethodId(), r.toPaymentMethodName(),
                        r.merchant(), r.memo()
                ))
                .toList();

        var top = dashboardMapper.selectTopExpenseCategories(ledgerId, from, to, 5).stream()
                .map(r -> new DashboardResponse.CategoryStat(r.categoryId(), r.name(), r.icon(), r.amount()))
                .toList();

        return new DashboardResponse(
                new DashboardResponse.Period(from, to),
                new DashboardResponse.Summary(income, expense, net, s.incomeCount(), s.expenseCount()),
                recent,
                top
        );
    }
}
