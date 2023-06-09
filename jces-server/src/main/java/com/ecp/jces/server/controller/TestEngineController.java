package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.terminal.TestEngineService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestEngineVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testEngine")
public class TestEngineController extends Base {
    @Autowired
    private TestEngineService testEngineService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody TestEngineForm testEngineForm) {
        testEngineService.add(testEngineForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody TestEngineForm testEngineForm) {
        VerificationUtils.string("id", testEngineForm.getId(), false, 36);
        testEngineService.update(testEngineForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody TestEngineForm testEngineForm) {
        VerificationUtils.string("id", testEngineForm.getId(), false, 36);
        testEngineService.delete(testEngineForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody TestEngineForm testEngineForm) {
        VerificationUtils.integer("page", testEngineForm.getPage());
        VerificationUtils.integer("pageCount", testEngineForm.getPageCount());
        Pagination<TestEngineVo> pagination = testEngineService.page(testEngineForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/getById", produces = GlobalContext.PRODUCES)
    public String getById(@RequestBody TestEngineForm testEngineForm){
        VerificationUtils.string("id",testEngineForm.getId(),false,36);
        return respString(ResultCode.Success,ResultCode.SUCCESS,testEngineService.getById(testEngineForm));
    }

    @PostMapping(value = "/updateStatus", produces = GlobalContext.PRODUCES)
    public String updateStatus(@RequestBody TestEngineForm testEngineForm) {
        VerificationUtils.string("id", testEngineForm.getId(), false, 36);
        testEngineService.updateStatus(testEngineForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }
}
