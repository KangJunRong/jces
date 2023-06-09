package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.AppletForm;
import com.ecp.jces.form.TestReportApduForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.applet.AppletService;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.AppletVo;
import com.ecp.jces.vo.TestReportApduVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applet")
public class AppletController extends Base {
    @Autowired
    private AppletService appletService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody AppletForm appletForm) {
        VerificationUtils.string("name", appletForm.getName());
        VerificationUtils.string("cos版本", appletForm.getVersionNo());
        appletService.add(appletForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody AppletForm appletForm) {
        VerificationUtils.string("id", appletForm.getId());
        appletService.edit(appletForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    // 终止测试
    @PostMapping(value = "/terminateTest", produces = GlobalContext.PRODUCES)
    public String terminateTest(@RequestBody AppletForm form){
        appletService.terminateTest(form);
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody AppletForm appletForm) {
        VerificationUtils.string("id", appletForm.getId());
        appletService.del(appletForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody AppletForm appletForm) {
        VerificationUtils.integer("page", appletForm.getPage());
        VerificationUtils.integer("pageCount", appletForm.getPageCount());
        Pagination<AppletVo> pagination = appletService.page(appletForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/get", produces = GlobalContext.PRODUCES)
    public String get(@RequestBody AppletForm appletForm) {
        VerificationUtils.string("id", appletForm.getId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, appletService.findById(appletForm.getId()));
    }

    @PostMapping(value = "/commitCap", produces = GlobalContext.PRODUCES)
    public String commitCap(@RequestBody AppletForm appletForm) {
        VerificationUtils.string("id", appletForm.getId());
        VerificationUtils.obj("capPath", appletForm.getCapPath());
        appletService.commitCap(appletForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }


    @PostMapping(value = "/appTest", produces = GlobalContext.PRODUCES)
    public String appTest(@RequestBody AppletForm form) {
        VerificationUtils.string("id", form.getId());
        appletService.appTest(form.getId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/rejectTest", produces = GlobalContext.PRODUCES)
    public String rejectTest(@RequestBody AppletForm form) {
        VerificationUtils.string("id", form.getId());
        appletService.rejectTest(form.getId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/commitTest", produces = GlobalContext.PRODUCES)
    public String commitTest(@RequestBody AppletForm form) {
        VerificationUtils.string("id", form.getId());

        if(StrUtil.isNotBlank(form.getTimeOut())){
            if(form.getTimeOut() <5 ){
                throw new FrameworkRuntimeException(ResultCode.Fail, "超时时间不能小于5分钟");
            }
            if(form.getTimeOut() > 9999){
                throw new FrameworkRuntimeException(ResultCode.Fail, "超时时间不能大于9999分钟");
            }
        }
        appletService.commitTest(form.getId(),form.getTimeOut());
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/appletCreateCount", produces = GlobalContext.PRODUCES)
    public String appletCreateCount(@RequestBody AppletForm form) {
        List<Map<String, Object>> data = appletService.appletCreateCount(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }

    @PostMapping(value = "/testDetail", produces = GlobalContext.PRODUCES)
    public String testDetail(@RequestBody AppletForm form) {
        VerificationUtils.string("id", form.getId());
        Map<String, Object> data = appletService.testDetail(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }

    @PostMapping(value = "/testApduDetail", produces = GlobalContext.PRODUCES)
    public String testApduDetail(@RequestBody TestReportApduForm form) {
        List<TestReportApduVo> data = appletService.testApduDetail(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }

    /**
     * 审核列表
     * @param appletForm
     * @return
     */
    @PostMapping(value = "/examinePage", produces = GlobalContext.PRODUCES)
    public String examinePage(@RequestBody AppletForm appletForm) {
        VerificationUtils.integer("page", appletForm.getPage());
        VerificationUtils.integer("pageCount", appletForm.getPageCount());
        Pagination<AppletVo> pagination = appletService.examinePage(appletForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    /**
     * 下载文件
     *
     * @param response
     * @param id
     * @throws IOException
     */
    @RequestMapping(value = "/pdfReport", produces = GlobalContext.PRODUCES)
    public void pdfReport(HttpServletResponse response,
                             @RequestParam(name = "id") String id) throws IOException {
        appletService.pdfReport(response, id);
    }

    /**
     * 安全审核员下载文件
     *
     * @param response
     * @param id
     * @throws IOException
     */
    @RequestMapping(value = "/pdfReportSave", produces = GlobalContext.PRODUCES)
    public void pdfReportSave(HttpServletResponse response,
                          @RequestParam(name = "id") String id) throws IOException {
        appletService.pdfReportSave(response, id);
    }
}
