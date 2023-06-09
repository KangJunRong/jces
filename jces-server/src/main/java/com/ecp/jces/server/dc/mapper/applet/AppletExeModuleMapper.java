package com.ecp.jces.server.dc.mapper.applet;

import com.ecp.jces.form.AppletExeModuleForm;
import com.ecp.jces.form.AppletInstanceForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface AppletExeModuleMapper {
    void del(@Param("id") String id);
    void add(AppletExeModuleForm form);
    void edit(AppletExeModuleForm form);

    void addInstance(AppletInstanceForm form);
    void updateInstanceInstallParam(@Param("id") String id,@Param("installParam") String installParam);
}