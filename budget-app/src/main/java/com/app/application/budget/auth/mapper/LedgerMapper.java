package com.app.application.budget.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.UUID;

@Mapper
public interface LedgerMapper {

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
}
