package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestBusinessScriptForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.applet.TestBusinessScriptService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.SpecificationVo;
import com.ecp.jces.vo.TestBusinessScriptVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testBusinessScript")
public class TestBusinessScriptController extends Base {
    @Autowired
    private TestBusinessScriptService testBusinessScriptService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody TestBusinessScriptForm form) {
        VerificationUtils.string("appletId", form.getAppletId());
        VerificationUtils.string("path", form.getPath(), false, 1024);
        VerificationUtils.integer("version", form.getVersion());
        testBusinessScriptService.add(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody TestBusinessScriptForm form) {
        VerificationUtils.string("id", form.getId());
        testBusinessScriptService.edit(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody TestBusinessScriptForm form) {
        VerificationUtils.string("id", form.getId());
        testBusinessScriptService.del(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody TestBusinessScriptForm form) {
        VerificationUtils.integer("page", form.getPage());
        VerificationUtils.integer("pageCount", form.getPageCount());
        Pagination<TestBusinessScriptVo> pagination = testBusinessScriptService.page(form);
        if (pagination.getData() != null && pagination.getData().size() > 0) {
            for(TestBusinessScriptVo vo : pagination.getData()){
                if(StrUtil.isNotBlank(vo.getPath())){
                    vo.setPath(AesUtil2.encryptData(vo.getPath()));
                }
                if(StrUtil.isNotBlank(vo.getLogPath())){
                    vo.setLogPath(AesUtil2.encryptData(vo.getLogPath()));
                }
            }
        }
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/get", produces = GlobalContext.PRODUCES)
    public String get(@RequestBody TestBusinessScriptForm form) {
        VerificationUtils.string("id", form.getId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, testBusinessScriptService.findById(form.getId()));
    }

    @PostMapping(value = "/findByAppletId", produces = GlobalContext.PRODUCES)
    public String findByAppletId(@RequestBody TestBusinessScriptForm form) {
        VerificationUtils.string("appletId", form.getAppletId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, testBusinessScriptService.findByAppletId(form.getAppletId()));
    }


    @PostMapping(value = "/getLastVersion", produces = GlobalContext.PRODUCES)
    public String getLastVersion(@RequestBody TestBusinessScriptForm form) {
        VerificationUtils.string("appletId", form.getAppletId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, testBusinessScriptService.getLastVersion(form.getAppletId()));
    }

    @PostMapping(value = "/commitPretest", produces = GlobalContext.PRODUCES)
    public String commitPretest(@RequestBody TestBusinessScriptForm form) {
        VerificationUtils.string("appletId", form.getAppletId());
        testBusinessScriptService.commitPretest(form.getAppletId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }
}
