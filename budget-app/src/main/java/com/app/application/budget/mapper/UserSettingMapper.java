package com.app.application.budget.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

@Mapper
public interface UserSettingMapper {

  /**
   * 사용자 설정 초기값 삽입
   * @param userId
   * @param ledgerId
   * @param locale
   * @param timezone
   * @param currency
   * @param notifyFreq
   * @return
   */
    @Insert("""
        INSERT INTO user_setting (
          user_id, default_ledger_id, locale, timezone, currency_code,
          dark_mode, notify_budget, notify_uncategorized, notify_recurring_detect
        )
        VALUES (
          #{userId}, #{ledgerId}, #{locale}, #{timezone}, #{currency},
          true, true, #{notifyFreq}::notify_freq, true
        )
    """)
    int insert(
            @Param("userId") UUID userId,
            @Param("ledgerId") UUID ledgerId,
            @Param("locale") String locale,
            @Param("timezone") String timezone,
            @Param("currency") String currency,
            @Param("notifyFreq") String notifyFreq // "WEEKLY"
    );
}
