package com.app.application.budget.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.UUID;

@Mapper
public interface LedgerMemberMapper {

    /**
     * 원장 멤버 추가
     * @param ledgerId
     * @param userId
     * @param role
     * @return
     */
    @Insert("""
        INSERT INTO ledger_member (ledger_id, user_id, role)
        VALUES (#{ledgerId}, #{userId}, #{role}::user_role)
    """)
    int insert(
            @Param("ledgerId") UUID ledgerId,
            @Param("userId") UUID userId,
            @Param("role") String role // "OWNER"
    );

    @Select("""
        SELECT EXISTS(
        SELECT 1
        FROM ledger_member
        WHERE ledger_id = #{ledgerId}
        AND user_id = #{userId}
        AND deleted_at IS NULL
        )
    """)
    boolean existsMember(@Param("ledgerId") UUID ledgerId, @Param("userId") UUID userId);
}
