package com.ecp.jces.server.dc.service.sys;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.SysConfigForm;
import com.ecp.jces.vo.SysConfigVo;

import java.util.List;

public interface SysConfigService {

    List<SysConfigVo> findList(SysConfigForm sysConfigForm) throws FrameworkRuntimeException;

    Pagination<SysConfigVo> page(SysConfigForm sysConfigForm) throws FrameworkRuntimeException;

    void add(SysConfigForm sysConfigForm) throws FrameworkRuntimeException;

    void update(SysConfigForm sysConfigForm) throws FrameworkRuntimeException;

    void delete(SysConfigForm sysConfigForm) throws FrameworkRuntimeException;

    SysConfigVo getByLabel(SysConfigForm sysConfigForm) throws FrameworkRuntimeException;

}
