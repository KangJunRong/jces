package com.ecp.jces.server.dc.mapper.applet;

import com.ecp.jces.form.TestCheckReportForm;
import com.ecp.jces.vo.TestCheckReportVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TestCheckReportMapper {
    int del(String id);
    int add(TestCheckReportForm form);
    int edit(TestCheckReportForm form);
    TestCheckReportVo findByAppletIdAndVersionId(@Param("appletId")String appletId,@Param("appletVersionId")String appletVersionId);

    int updateSensitiveApi(@Param("appletId")String appletId,
                            @Param("appletVersionId")String appletVersionId,
                            @Param("sensitiveApi")String sensitiveApi);
}
