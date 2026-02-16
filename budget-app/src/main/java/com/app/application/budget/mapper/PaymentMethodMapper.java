package com.app.application.budget.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

@Mapper
public interface PaymentMethodMapper {

    /**
     * 결제 수단 추가
     * @param ledgerId
     * @param type
     * @param name
     * @param institution
     * @param last4
     * @return
     */
    @Insert("""
        INSERT INTO payment_method (ledger_id, type, name, institution, last4, is_active)
        VALUES (#{ledgerId}, #{type}::payment_method_type, #{name}, #{institution}, #{last4}, true)
    """)
    int insert(
            @Param("ledgerId") UUID ledgerId,
            @Param("type") String type, // CARD/CASH/BANK/TRANSIT
            @Param("name") String name,
            @Param("institution") String institution,
            @Param("last4") String last4
    );
}
