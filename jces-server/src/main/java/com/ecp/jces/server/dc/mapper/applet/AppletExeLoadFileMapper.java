package com.ecp.jces.server.dc.mapper.applet;

import com.ecp.jces.form.AppletExeLoadFileForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface AppletExeLoadFileMapper {

    void del(@Param("id") String id);

    void add(AppletExeLoadFileForm form);

    void edit(AppletExeLoadFileForm form);

}