package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.SysConfigForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.sys.SysConfigService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.SysConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sysConfig")
public class SysConfigController extends Base {
    @Autowired
    private SysConfigService sysConfigService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody SysConfigForm sysConfigForm) {
        sysConfigService.add(sysConfigForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody SysConfigForm sysConfigForm) {
        sysConfigService.update(sysConfigForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody SysConfigForm sysConfigForm) {
        VerificationUtils.string("id", sysConfigForm.getId(), false, 36);
        sysConfigService.delete(sysConfigForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody SysConfigForm sysConfigForm) {
        VerificationUtils.integer("page", sysConfigForm.getPage());
        VerificationUtils.integer("pageCount", sysConfigForm.getPageCount());
        Pagination<SysConfigVo> pagination = sysConfigService.page(sysConfigForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "getByLabel", produces = GlobalContext.PRODUCES)
    public String getByLabel(@RequestBody SysConfigForm sysConfigForm) {
        VerificationUtils.string("label", sysConfigForm.getLabel());
        return respString(ResultCode.Success,ResultCode.SUCCESS,sysConfigService.getByLabel(sysConfigForm));
    }

}
