package com.app.application.budget.mapper;

import com.app.application.budget.common.mybatis.UuidTypeHandler;
import com.app.application.budget.record.RecentTxRecord;
import com.app.application.budget.record.SummaryRecord;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface TxSummaryMapper {

    @Select("""
        SELECT
          COALESCE(SUM(CASE WHEN type='INCOME'  THEN amount ELSE 0 END), 0) AS income,
          COALESCE(SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END), 0) AS expense,
          COUNT(*) FILTER (WHERE type='INCOME')  AS incomeCount,
          COUNT(*) FILTER (WHERE type='EXPENSE') AS expenseCount
        FROM tx
        WHERE ledger_id = #{ledgerId}
          AND deleted_at IS NULL
          AND status <> 'VOID'
          AND occurred_at >= #{from}
          AND occurred_at <  #{to}
    """)
    SummaryRecord sumIncomeExpense(@Param("ledgerId") UUID ledgerId,
                               @Param("from") OffsetDateTime from,
                               @Param("to") OffsetDateTime to);

    @Select("""
        SELECT
          id::text AS id,
          occurred_at AS occurredAt,
          type,
          amount,
          COALESCE(currency_code, '') AS currencyCode,
          memo
        FROM tx
        WHERE ledger_id = #{ledgerId}
          AND deleted_at IS NULL
          AND status <> 'VOID'
        ORDER BY occurred_at DESC, created_at DESC
        LIMIT #{limit}
    """)
    @ConstructorArgs({
        @Arg(column="id", javaType=UUID.class, jdbcType=JdbcType.VARCHAR, typeHandler=UuidTypeHandler.class),
        @Arg(column="occurredAt", javaType=OffsetDateTime.class),
        @Arg(column="type", javaType=String.class),
        @Arg(column="amount", javaType=Long.class),
        @Arg(column="currencyCode", javaType=String.class),
        @Arg(column="memo", javaType=String.class)
    })
    List<RecentTxRecord> findRecent(@Param("ledgerId") UUID ledgerId,
                                @Param("limit") int limit);
}
