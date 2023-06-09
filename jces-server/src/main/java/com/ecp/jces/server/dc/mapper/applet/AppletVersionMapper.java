package com.ecp.jces.server.dc.mapper.applet;


import com.ecp.jces.form.AppletVersionForm;
import com.ecp.jces.form.extra.StartTestForm;
import com.ecp.jces.vo.AppletVersionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppletVersionMapper {
    int del(@Param("id") String id);

    int add(AppletVersionForm form);

    AppletVersionVo findById(@Param("id") String id);

    int edit(AppletVersionForm form);

    List<AppletVersionVo> list(AppletVersionForm form);

    Integer getCapLastVersion(@Param("appletId")String appletId);

    List<Map<String, Object>> appletVersionCreateCount(AppletVersionForm form);

    StartTestForm findByTaskId(@Param("taskId") String taskId);
}