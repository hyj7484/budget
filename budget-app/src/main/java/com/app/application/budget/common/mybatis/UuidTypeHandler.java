package com.app.application.budget.common.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.UUID;

@MappedTypes(UUID.class)
public class UuidTypeHandler extends BaseTypeHandler<UUID> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        // Postgres uuid에 가장 안전한 방식
        ps.setObject(i, parameter);
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toUuid(rs.getObject(columnName));
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toUuid(rs.getObject(columnIndex));
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toUuid(cs.getObject(columnIndex));
    }

    private UUID toUuid(Object v) {
        if (v == null) return null;
        if (v instanceof UUID u) return u;
        if (v instanceof String s) return UUID.fromString(s);

        // 어떤 드라이버/설정에서는 uuid가 16바이트로 들어올 수 있음
        if (v instanceof byte[] b) {
            ByteBuffer bb = ByteBuffer.wrap(b);
            long high = bb.getLong();
            long low = bb.getLong();
            return new UUID(high, low);
        }

        // 마지막 안전망
        return UUID.fromString(v.toString());
    }
}
