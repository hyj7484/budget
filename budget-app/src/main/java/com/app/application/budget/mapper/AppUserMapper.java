package com.app.application.budget.mapper;

import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.app.application.budget.auth.dto.AppUserAuthRow;


@Mapper
public interface AppUserMapper {

    /**
     * 사용자 생성 및 ID 반환
     * @param email
     * @param passwordHash
     * @param displayName
     * @param locale
     * @param timezone
     * @param currency
     * @return
     */
    @Select("""
    INSERT INTO app_user (email, password_hash, display_name, locale, timezone, default_currency)
    VALUES (#{email}, #{passwordHash}, #{displayName}, #{locale}, #{timezone}, #{currency})
    RETURNING id::text
    """)
    @org.apache.ibatis.annotations.ResultType(UUID.class)
    UUID insertAndReturnId(
        @Param("email") String email,
        @Param("passwordHash") String passwordHash,
        @Param("displayName") String displayName,
        @Param("locale") String locale,
        @Param("timezone") String timezone,
        @Param("currency") String currency
    );

@Select("""
        SELECT
          id::text            AS id,
          email               AS email,
          password_hash       AS "passwordHash",
          display_name        AS "displayName"
        FROM app_user
        WHERE email = #{email}
          AND deleted_at IS NULL
    """)
    @org.apache.ibatis.annotations.ConstructorArgs({
        @org.apache.ibatis.annotations.Arg(column = "id", javaType = UUID.class, typeHandler = com.app.application.budget.common.mybatis.UuidTypeHandler.class),
        @org.apache.ibatis.annotations.Arg(column = "email", javaType = String.class),
        @org.apache.ibatis.annotations.Arg(column = "passwordHash", javaType = String.class),
        @org.apache.ibatis.annotations.Arg(column = "displayName", javaType = String.class)
    })
    AppUserAuthRow findAuthByEmail(@org.apache.ibatis.annotations.Param("email") String email);

}
