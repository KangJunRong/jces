package com.ecp.jces.server.dc.service.applet;


import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.AppletVersionForm;
import com.ecp.jces.vo.AppletVersionVo;

import java.util.List;
import java.util.Map;


public interface AppletVersionService {

    AppletVersionVo findById(String id) throws FrameworkRuntimeException;

    List<AppletVersionVo> list(AppletVersionForm form) throws FrameworkRuntimeException;

    Pagination<AppletVersionVo> page(AppletVersionForm form) throws FrameworkRuntimeException;

    void add(AppletVersionForm form) throws FrameworkRuntimeException;

    void edit(AppletVersionForm form) throws FrameworkRuntimeException;

    void del(AppletVersionForm form) throws FrameworkRuntimeException;

    Integer getCapLastVersion(String id) throws FrameworkRuntimeException;

    List<Map<String, Object>> appletVersionCreateCount(AppletVersionForm form) throws FrameworkRuntimeException;
}
