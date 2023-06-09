package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestCardGroupForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.terminal.TestCardGroupService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestCardGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testCardGroup")
public class TestCardGroupController extends Base {
    @Autowired
    private TestCardGroupService testCardGroupService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody TestCardGroupForm testCardGroupForm) {
        testCardGroupService.add(testCardGroupForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody TestCardGroupForm testCardGroupForm) {
        testCardGroupService.update(testCardGroupForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody TestCardGroupForm testCardGroupForm) {
        VerificationUtils.string("id", testCardGroupForm.getId(), false, 36);
        testCardGroupService.delete(testCardGroupForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody TestCardGroupForm testCardGroupForm) {
        VerificationUtils.integer("page", testCardGroupForm.getPage());
        VerificationUtils.integer("pageCount", testCardGroupForm.getPageCount());
        Pagination<TestCardGroupVo> pagination = testCardGroupService.page(testCardGroupForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }
    @PostMapping(value = "/publish", produces = GlobalContext.PRODUCES)
    public String publish(@RequestBody TestCardGroupForm testCardGroupForm){
        testCardGroupService.publish(testCardGroupForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/active", produces = GlobalContext.PRODUCES)
    public String active(@RequestBody TestCardGroupForm testCardGroupForm){
        testCardGroupService.active(testCardGroupForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/get", produces = GlobalContext.PRODUCES)
    public String get(@RequestBody TestCardGroupForm testCardGroupForm){
        TestCardGroupVo data = testCardGroupService.getById(testCardGroupForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }

}
