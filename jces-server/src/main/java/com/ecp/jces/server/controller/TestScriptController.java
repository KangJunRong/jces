package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestBusinessScriptForm;
import com.ecp.jces.form.TestCardGroupForm;
import com.ecp.jces.form.TestScriptForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.sysScript.TestScriptService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestBusinessScriptVo;
import com.ecp.jces.vo.TestScriptVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testScript")
public class TestScriptController extends Base {
    @Autowired
    private TestScriptService testScriptService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody TestScriptForm form) {
        VerificationUtils.string("path", form.getPath(), false, 1024);
        testScriptService.add(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }


    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody TestScriptForm form) {
        VerificationUtils.string("id", form.getId());
        testScriptService.delete(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody TestScriptForm form) {
        VerificationUtils.integer("page", form.getPage());
        VerificationUtils.integer("pageCount", form.getPageCount());
        Pagination<TestScriptVo> pagination = testScriptService.page(form);
        if (pagination.getData() != null && pagination.getData().size() > 0) {
            for(TestScriptVo vo : pagination.getData()){
                if(StrUtil.isNotBlank(vo.getPath())){
                    vo.setPath(AesUtil2.encryptData(vo.getPath()));
                }
            }
        }
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/getVersion", produces = GlobalContext.PRODUCES)
    public String getVersion(@RequestBody TestScriptForm form) {
        return respString(ResultCode.Success, ResultCode.SUCCESS, testScriptService.getVersion());
    }

    @PostMapping(value = "/active", produces = GlobalContext.PRODUCES)
    public String active(@RequestBody TestScriptForm form){
        testScriptService.active(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

}
