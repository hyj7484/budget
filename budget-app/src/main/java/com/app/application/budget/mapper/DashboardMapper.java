package com.app.application.budget.mapper;

import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

import com.app.application.budget.domain.enums.PaymentMethodType;
import com.app.application.budget.record.CategoryStatRecord;
import com.app.application.budget.record.RecentTxRecord;
import com.app.application.budget.record.SummaryRecord;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface DashboardMapper {


    @Lang(XMLLanguageDriver.class)
    @Select("""
        <script>
        SELECT
          COALESCE(SUM(CASE WHEN tx.type='INCOME'  THEN tx.amount ELSE 0 END), 0) AS income,
          COALESCE(SUM(CASE WHEN tx.type='EXPENSE' THEN tx.amount ELSE 0 END), 0) AS expense,
          ( COALESCE(SUM(CASE WHEN tx.type='INCOME'  THEN tx.amount ELSE 0 END), 0)
          - COALESCE(SUM(CASE WHEN tx.type='EXPENSE' THEN tx.amount ELSE 0 END), 0) ) AS net,
          COUNT(*) FILTER (WHERE tx.type='INCOME')  AS incomeCount,
          COUNT(*) FILTER (WHERE tx.type='EXPENSE') AS expenseCount
        FROM tx
        LEFT JOIN payment_method pm ON pm.id = tx.payment_method_id
        WHERE tx.ledger_id = #{ledgerId}
        <![CDATA[
        AND tx.status <> 'VOID'
        AND tx.deleted_at IS NULL
        AND tx.occurred_at >= #{from}
        AND tx.occurred_at < #{to}
        ]]>
        <if test="paymentMethodType != null">
        AND pm.type::text = #{paymentMethodType}
        </if>
        </script>
    """)
    SummaryRecord sumIncomeExpense(@Param("ledgerId") UUID ledgerId,
                               @Param("from") OffsetDateTime from,
                               @Param("to") OffsetDateTime to,
                               @Param("paymentMethodType") PaymentMethodType paymentMethodType
                               );

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
    List<RecentTxRecord> selectRecent(@Param("ledgerId") UUID ledgerId, @Param("limit") int limit);

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
    List<CategoryStatRecord> selectTopExpenseCategories(@Param("ledgerId") UUID ledgerId,
                                                    @Param("from") OffsetDateTime from,
                                                    @Param("to") OffsetDateTime to,
                                                    @Param("limit") int limit);
}