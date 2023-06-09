package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestEngineReaderForm;
import com.ecp.jces.form.extra.CardInfoForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.terminal.TestEngineReaderService;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestEngineReaderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/testEngineReader")
public class TestEngineReaderController extends Base {
    @Autowired
    private TestEngineReaderService testEngineReaderService;

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody TestEngineReaderForm testEngineReaderForm) {
        VerificationUtils.integer("page", testEngineReaderForm.getPage());
        VerificationUtils.integer("pageCount", testEngineReaderForm.getPageCount());
        Pagination<TestEngineReaderVo> pagination = testEngineReaderService.page(testEngineReaderForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    /** 同步引擎下的读卡器 **/
    /*@PostMapping(value = "/syncReaders", produces = GlobalContext.PRODUCES)
    public String getReaders(@RequestBody TestEngineReaderForm testEngineReaderForm) {
        VerificationUtils.string("engineId", testEngineReaderForm.getTestEngine().getId(), false, 36);
        testEngineReaderService.syncReaders(testEngineReaderForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }
*/
    /** 引擎下的读卡器设为离线 **/
    @PostMapping(value = "/setReadersOffLine", produces = GlobalContext.PRODUCES)
    public String setReadersOffLine(@RequestBody TestEngineReaderForm testEngineReaderForm) {
        VerificationUtils.string("engineId", testEngineReaderForm.getTestEngine().getId(), false, 36);
        testEngineReaderService.setReadersOffLine(testEngineReaderForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }
}
