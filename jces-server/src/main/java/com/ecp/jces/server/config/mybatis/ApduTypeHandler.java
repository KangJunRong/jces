package com.ecp.jces.server.config.mybatis;

import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.form.extra.ApduDataForm;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * @author kangjunrong
 * @date 2019年4月29日
 */
public class ApduTypeHandler extends BaseTypeHandler<List<ApduDataForm>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<ApduDataForm> parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter != null && !parameter.isEmpty()) {
            ps.setString(i, JSONUtils.toJSONString(parameter));
        } else {
            ps.setString(i, null);
        }
    }

    @Override
    public List<ApduDataForm> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        List<ApduDataForm> list;
        String str = rs.getString(columnName);
        if (rs.wasNull()) {
            return null;
        }
        list = JSONUtils.parseArray(str, ApduDataForm.class);
        return list;
    }

    @Override
    public List<ApduDataForm> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        List<ApduDataForm> list;
        String str = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        list = JSONUtils.parseArray(str, ApduDataForm.class);
        return list;
    }

    @Override
    public List<ApduDataForm> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        List<ApduDataForm> list;
        String str = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        list = JSONUtils.parseArray(str, ApduDataForm.class);
        return list;
    }

}
