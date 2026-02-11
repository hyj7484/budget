package com.app.application.budget.auth.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

@Mapper
public interface LedgerMemberMapper {

    @Insert("""
        INSERT INTO ledger_member (ledger_id, user_id, role)
        VALUES (#{ledgerId}, #{userId}, #{role}::user_role)
    """)
    int insert(
            @Param("ledgerId") UUID ledgerId,
            @Param("userId") UUID userId,
            @Param("role") String role // "OWNER"
    );
}
