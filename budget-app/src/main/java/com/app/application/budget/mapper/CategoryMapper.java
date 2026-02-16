package com.app.application.budget.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.app.application.budget.domain.enums.CategoryKind;

import java.util.UUID;

@Mapper
public interface CategoryMapper {

    @Insert("""
        INSERT INTO category (ledger_id, kind, parent_id, name, icon, sort_order)
        VALUES (#{ledgerId}, #{kind}::category_kind, NULL, #{name}, #{icon}, #{sortOrder})
    """)
    int insertRoot(
            @Param("ledgerId") UUID ledgerId,
            @Param("kind") CategoryKind kind, // EXPENSE/INCOME
            @Param("name") String name,
            @Param("icon") String icon,
            @Param("sortOrder") int sortOrder
    );
}
