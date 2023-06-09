package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestMatrixForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.terminal.TestMatrixService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestMatrixVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/testMatrix")
public class TestMatrixController extends Base {
    @Autowired
    private TestMatrixService testMatrixService;

    @PostMapping(value = "/updateStatus" ,produces = GlobalContext.PRODUCES)
    public String updateStatus(@RequestBody TestMatrixForm form){
        VerificationUtils.string("matrixId",form.getMatrixId());
        VerificationUtils.string("matrixStatus",form.getMatrixStatus());
        testMatrixService.updateStatus(form);
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }

    @PostMapping(value = "getTestMatrixInformation" ,produces = GlobalContext.PRODUCES)
    public String getTestMatrixInformation(@RequestBody TestMatrixForm form){
        String engineId = form.getEngineId();
        VerificationUtils.string("engineId",engineId);
        testMatrixService.getTestMatrixInformation(engineId);
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }

    @PostMapping(value = "getMatrixInfo", produces = GlobalContext.PRODUCES)
    public String getMatrixInfo (@RequestBody TestMatrixForm form){
        VerificationUtils.string("matrixId",form.getMatrixId());
        VerificationUtils.string("engineId",form.getEngineId());
        return respString(ResultCode.Success,ResultCode.SUCCESS, testMatrixService.getMatrixInfo(form));
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody TestMatrixForm form){
        VerificationUtils.integer("page",form.getPage());
        VerificationUtils.integer("pageCount",form.getPageCount());
        Pagination<TestMatrixVo> pagination = testMatrixService.page(form);
        return respString(ResultCode.Success,ResultCode.SUCCESS,pagination);
    }

    @PostMapping(value = "/getListByEngineId", produces = GlobalContext.PRODUCES)
    public String getListByEngineId(@RequestBody TestMatrixForm form){
        VerificationUtils.string("engineId", form.getEngineId());
        return respString(ResultCode.Success,ResultCode.SUCCESS,testMatrixService.getListByEngineId(form));
    }

}
