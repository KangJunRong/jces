package com.ecp.jces.server.dc.mapper.applet;

import com.ecp.jces.form.AppletForm;
import com.ecp.jces.vo.AppletExeLoadFileVo;
import com.ecp.jces.vo.AppletVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppletMapper {

    int del(@Param("id") String id);

    int add(AppletForm form);

    AppletVo findById(@Param("id") String id);

    int edit(AppletForm form);

    List<AppletVo> list(AppletForm form);

    AppletVo findByName(@Param("name")String name);

    List<Map<String, Object>> appletCreateCount(AppletForm form);

    List<AppletExeLoadFileVo> getLoadFiles(@Param("id")String id, @Param("versionId")String versionId);

    AppletVo detail(AppletForm form);

    void delBusinessScript(@Param("id")String id);

    int findTestCountByUserId(@Param("userId")String userId);

    List<AppletVo> examineList(AppletForm form);
}