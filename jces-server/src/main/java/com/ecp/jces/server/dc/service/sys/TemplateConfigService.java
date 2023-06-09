package com.ecp.jces.server.dc.service.sys;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TemplateConfigForm;
import com.ecp.jces.vo.TemplateConfigVo;

import java.util.List;

public interface TemplateConfigService {

    Pagination<TemplateConfigVo> page(TemplateConfigForm form) throws FrameworkRuntimeException;

    void add(TemplateConfigForm form) throws FrameworkRuntimeException;

    void update(TemplateConfigForm form) throws FrameworkRuntimeException;

    void delete(TemplateConfigForm form) throws FrameworkRuntimeException;

    TemplateConfigVo getById(TemplateConfigForm form) throws FrameworkRuntimeException;

    List<TemplateConfigVo> list(TemplateConfigForm form) throws FrameworkRuntimeException;
}
