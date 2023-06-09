package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestCardForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.terminal.TestCardService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestCardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testCard")
public class TestCardController extends Base {
    @Autowired
    private TestCardService testCardService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody TestCardForm testCardForm) {
        testCardService.add(testCardForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody TestCardForm testCardForm) {
        testCardService.update(testCardForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody TestCardForm testCardForm) {
        VerificationUtils.string("id", testCardForm.getId(), false, 36);
        testCardService.delete(testCardForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody TestCardForm testCardForm) {
        VerificationUtils.integer("page", testCardForm.getPage());
        VerificationUtils.integer("pageCount", testCardForm.getPageCount());
        Pagination<TestCardVo> pagination = testCardService.page(testCardForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/pageByCardGroup", produces = GlobalContext.PRODUCES)
    public String pageByCardGroup(@RequestBody TestCardForm testCardForm) {
        VerificationUtils.integer("page", testCardForm.getPage());
        VerificationUtils.integer("pageCount", testCardForm.getPageCount());
        Pagination<TestCardVo> pagination = testCardService.pageByCardGroup(testCardForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }



}
