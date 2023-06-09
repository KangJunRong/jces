package com.ecp.jces.server.dc.service.sysScript;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestScriptForm;
import com.ecp.jces.vo.TestScriptVo;


public interface TestScriptService {


    Pagination<TestScriptVo> page(TestScriptForm testScriptForm) throws FrameworkRuntimeException;

    void add(TestScriptForm testScriptForm) throws FrameworkRuntimeException;

    void delete(TestScriptForm testScriptForm) throws FrameworkRuntimeException;

    void active(TestScriptForm testScriptForm) throws FrameworkRuntimeException;

    Integer getVersion() throws FrameworkRuntimeException;

}
