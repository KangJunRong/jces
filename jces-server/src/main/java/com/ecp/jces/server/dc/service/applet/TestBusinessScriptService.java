package com.ecp.jces.server.dc.service.applet;


import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestBusinessScriptForm;
import com.ecp.jces.vo.TestBusinessScriptVo;

import java.util.List;


public interface TestBusinessScriptService {

    TestBusinessScriptVo findByAppletId(String appletId) throws FrameworkRuntimeException;

    TestBusinessScriptVo findById(String id) throws FrameworkRuntimeException;

    List<TestBusinessScriptVo> list(TestBusinessScriptForm form) throws FrameworkRuntimeException;

    Pagination<TestBusinessScriptVo> page(TestBusinessScriptForm form) throws FrameworkRuntimeException;

    void add(TestBusinessScriptForm form) throws FrameworkRuntimeException;

    void edit(TestBusinessScriptForm form) throws FrameworkRuntimeException;

    void del(TestBusinessScriptForm form) throws FrameworkRuntimeException;

    TestBusinessScriptVo getLastVersion(String appletId) throws FrameworkRuntimeException;

    void commitPretest(String appletId) throws FrameworkRuntimeException;
}
