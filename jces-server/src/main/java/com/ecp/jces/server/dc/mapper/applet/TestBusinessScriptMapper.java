package com.ecp.jces.server.dc.mapper.applet;

import com.ecp.jces.form.TestBusinessScriptForm;
import com.ecp.jces.vo.TestBusinessScriptVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestBusinessScriptMapper {
    int del(@Param("id")String id);

    int add(TestBusinessScriptForm form);

    TestBusinessScriptVo findById(@Param("id")String id);

    int edit(TestBusinessScriptForm form);

    List<TestBusinessScriptVo> list(TestBusinessScriptForm form);

    TestBusinessScriptVo getLastVersion(@Param("appletId")String appletId);
    TestBusinessScriptVo findByAppletId(@Param("appletId")String appletId);

    String findUserIdByLogPath(@Param("logPath")String logPath);
}