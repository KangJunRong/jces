package com.ecp.jces.server.dc.service.applet;


import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.AppletForm;
import com.ecp.jces.form.TestReportApduForm;
import com.ecp.jces.vo.AppletVo;
import com.ecp.jces.vo.TestReportApduVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface AppletService {

    AppletVo findById(String id) throws FrameworkRuntimeException;

    List<AppletVo> list(AppletForm form) throws FrameworkRuntimeException;

    Pagination<AppletVo> page(AppletForm form) throws FrameworkRuntimeException;

    void add(AppletForm form) throws FrameworkRuntimeException;

    void edit(AppletForm form) throws FrameworkRuntimeException;

    void del(AppletForm appletForm) throws FrameworkRuntimeException;

    void commitCap(AppletForm appletForm) throws FrameworkRuntimeException;

    void terminateTest(AppletForm appletForm) throws FrameworkRuntimeException;

    void appTest(String id) throws FrameworkRuntimeException;
    void rejectTest(String id) throws FrameworkRuntimeException;
    void commitTest(String id,Integer timeOut) throws FrameworkRuntimeException;

    List<Map<String, Object>> appletCreateCount(AppletForm form) throws FrameworkRuntimeException;

    Map<String, Object> testDetail(AppletForm form) throws FrameworkRuntimeException;

    List<TestReportApduVo> testApduDetail(TestReportApduForm form) throws FrameworkRuntimeException;

    Pagination<AppletVo> examinePage(AppletForm appletForm) throws FrameworkRuntimeException;

    void pdfReport(HttpServletResponse res, String id) throws FrameworkRuntimeException, IOException;

    void pdfReportSave(HttpServletResponse res, String id) throws FrameworkRuntimeException, IOException;

    Map<String, Object> testDetailApi(AppletForm form) throws FrameworkRuntimeException, IOException;
}
