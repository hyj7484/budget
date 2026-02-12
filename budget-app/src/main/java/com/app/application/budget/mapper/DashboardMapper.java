package com.app.application.budget.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface DashboardMapper {

    record SummaryRow(BigDecimal income, BigDecimal expense, long incomeCount, long expenseCount) {}

    @Select("""
        SELECT
          COALESCE(SUM(CASE WHEN type='INCOME'  THEN amount ELSE 0 END), 0) AS income,
          COALESCE(SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END), 0) AS expense,
          COUNT(*) FILTER (WHERE type='INCOME')  AS incomeCount,
          COUNT(*) FILTER (WHERE type='EXPENSE') AS expenseCount
        FROM tx
        WHERE ledger_id = #{ledgerId}
          AND status <> 'VOID'
          AND deleted_at IS NULL
          AND occurred_at >= #{from}
          AND occurred_at <  #{to}
    """)
    SummaryRow sumIncomeExpense(@Param("ledgerId") UUID ledgerId,
                               @Param("from") OffsetDateTime from,
                               @Param("to") OffsetDateTime to);

    record RecentTxRow(
            UUID id,
            String type,
            String status,
            OffsetDateTime occurredAt,
            BigDecimal amount,
            String currencyCode,
            UUID categoryId,
            String categoryName,
            String categoryIcon,
            UUID paymentMethodId,
            String paymentMethodName,
            UUID toPaymentMethodId,
            String toPaymentMethodName,
            String merchant,
            String memo
    ) {}

    @Select("""
        SELECT
          t.id,
          t.type,
          t.status,
          t.occurred_at AS occurredAt,
          t.amount,
          t.currency_code AS currencyCode,
          t.category_id AS categoryId,
          c.name AS categoryName,
          c.icon AS categoryIcon,
          t.payment_method_id AS paymentMethodId,
          pm.name AS paymentMethodName,
          t.to_payment_method_id AS toPaymentMethodId,
          pm2.name AS toPaymentMethodName,
          t.merchant,
          t.memo
        FROM tx t
        LEFT JOIN category c ON c.id = t.category_id
        LEFT JOIN payment_method pm ON pm.id = t.payment_method_id
        LEFT JOIN payment_method pm2 ON pm2.id = t.to_payment_method_id
        WHERE t.ledger_id = #{ledgerId}
          AND t.deleted_at IS NULL
        ORDER BY t.occurred_at DESC, t.created_at DESC
        LIMIT #{limit}
    """)
    List<RecentTxRow> selectRecent(@Param("ledgerId") UUID ledgerId, @Param("limit") int limit);

    record CategoryStatRow(UUID categoryId, String name, String icon, BigDecimal amount) {}

    @Select("""
        SELECT
          c.id AS categoryId,
          c.name,
          c.icon,
          COALESCE(SUM(t.amount), 0) AS amount
        FROM tx t
        JOIN category c ON c.id = t.category_id
        WHERE t.ledger_id = #{ledgerId}
          AND t.type = 'EXPENSE'
          AND t.status <> 'VOID'
          AND t.deleted_at IS NULL
          AND t.occurred_at >= #{from}
          AND t.occurred_at <  #{to}
        GROUP BY c.id, c.name, c.icon
        ORDER BY amount DESC
        LIMIT #{limit}
    """)
    List<CategoryStatRow> selectTopExpenseCategories(@Param("ledgerId") UUID ledgerId,
                                                    @Param("from") OffsetDateTime from,
                                                    @Param("to") OffsetDateTime to,
                                                    @Param("limit") int limit);
}
