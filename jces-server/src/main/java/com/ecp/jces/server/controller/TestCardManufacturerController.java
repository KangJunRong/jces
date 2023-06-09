package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestCardManufacturerForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.terminal.TestCardManufacturerService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestCardManufacturerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/testCardManufacturer")
public class TestCardManufacturerController extends Base {
    @Autowired
    private TestCardManufacturerService testCardManufacturerService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody TestCardManufacturerForm testCardManufacturerForm) {
        VerificationUtils.string("name", testCardManufacturerForm.getName(), false, 64);
        VerificationUtils.string("code", testCardManufacturerForm.getCode(), false, 3);
        testCardManufacturerService.add(testCardManufacturerForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody TestCardManufacturerForm testCardManufacturerForm) {
        VerificationUtils.string("name", testCardManufacturerForm.getName(), false, 64);
        VerificationUtils.string("code", testCardManufacturerForm.getCode(), false, 3);
        testCardManufacturerService.update(testCardManufacturerForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody TestCardManufacturerForm testCardManufacturerForm) {
        VerificationUtils.string("id", testCardManufacturerForm.getId(), false, 36);
        testCardManufacturerService.delete(testCardManufacturerForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody TestCardManufacturerForm testCardManufacturerForm) {
        VerificationUtils.integer("page", testCardManufacturerForm.getPage());
        VerificationUtils.integer("pageCount", testCardManufacturerForm.getPageCount());
        Pagination<TestCardManufacturerVo> pagination = testCardManufacturerService.page(testCardManufacturerForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/list", produces = GlobalContext.PRODUCES)
    public String list(@RequestBody TestCardManufacturerForm testCardManufacturerForm) {
        List<TestCardManufacturerVo> list = testCardManufacturerService.findList(testCardManufacturerForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, list);
    }
}
