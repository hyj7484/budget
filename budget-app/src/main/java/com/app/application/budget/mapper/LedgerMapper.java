package com.app.application.budget.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.app.application.budget.record.LedgerMetaRow;

import java.util.UUID;

@Mapper
public interface LedgerMapper {

    /**
     * 원장 생성 및 ID 반환
     * @param ownerUserId
     * @param name
     * @param currency
     * @param timezone
     * @return
     */
    @Select("""
        INSERT INTO ledger (owner_user_id, name, base_currency, timezone)
        VALUES (#{ownerUserId}, #{name}, #{currency}, #{timezone})
        RETURNING id :: text
    """)
    @org.apache.ibatis.annotations.ResultType(UUID.class)
    UUID insertAndReturnId(
            @Param("ownerUserId") UUID ownerUserId,
            @Param("name") String name,
            @Param("currency") String currency,
            @Param("timezone") String timezone
    );

    @Select("""
        SELECT id::text as id
        FROM ledger
        WHERE owner_user_id = #{userId}
          AND deleted_at IS NULL
        ORDER BY created_at ASC
        LIMIT 1
    """)
    @org.apache.ibatis.annotations.ResultType(UUID.class)
    UUID findDefaultLedgerId(@Param("userId") UUID userId);

    
    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM ledger_member
        WHERE ledger_id = #{ledgerId}
        AND user_id = #{userId}
        )
    """)
    boolean existsMember(@Param("ledgerId") UUID ledgerId, @Param("userId") UUID userId);

    @Select("""
        SELECT timezone, base_currency AS baseCurrency
        FROM ledger
        WHERE id = #{ledgerId}
        AND deleted_at IS NULL
    """)
    LedgerMetaRow findMeta(@Param("ledgerId") UUID ledgerId);
}
