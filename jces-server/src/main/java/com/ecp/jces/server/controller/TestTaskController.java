package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.form.AppletVersionForm;
import com.ecp.jces.form.TestReportDataForm;
import com.ecp.jces.form.TestScheduleForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.applet.TestTaskService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestReportDataVo;
import com.ecp.jces.vo.TestScheduleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/testTask")
public class TestTaskController extends Base {
    @Autowired
    private TestTaskService testTaskService;

    @PostMapping(value = "/testTaskCreateCount", produces = GlobalContext.PRODUCES)
    public String testTaskCreateCount(@RequestBody TestTaskForm form) {
        List<Map<String, Object>> data = testTaskService.testTaskCreateCount(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }

    @PostMapping(value = "/testTaskStatusCount", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody TestTaskForm form) {
        List<Map<String, Object>> data = testTaskService.testTaskStatusCount(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }


    @PostMapping(value = "/dataDetail", produces = GlobalContext.PRODUCES)
    public String dataDetail(@RequestBody TestScheduleForm form) {
        VerificationUtils.string("id", form.getId());
        List<TestReportDataVo> data = testTaskService.dataDetail(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }

    @PostMapping(value = "/installDetail", produces = GlobalContext.PRODUCES)
    public String installDetail(@RequestBody TestScheduleForm form) {
        VerificationUtils.string("id", form.getId());
        List<TestReportDataVo> data = testTaskService.installDetail(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }

    @PostMapping(value = "/findByTestBusinessScriptId" , produces = GlobalContext.PRODUCES)
    public String findByTestBusinessScriptId(@RequestBody TestTaskForm form){
        VerificationUtils.string("testBusinessScriptId" , form.getTestBusinessScriptId());
        return  respString(ResultCode.Success,ResultCode.SUCCESS,testTaskService.findByTestBusinessScriptId(form));
    }
}
