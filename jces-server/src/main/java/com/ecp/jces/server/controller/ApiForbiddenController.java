package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.form.ApiDownLoadForm;
import com.ecp.jces.form.ApiForbiddenForm;
import com.ecp.jces.form.ApiLinkForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.api.ApiForbiddenService;
import com.ecp.jces.server.util.VerificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/apiForbidden")
public class ApiForbiddenController extends Base{
    @Autowired
    private ApiForbiddenService apiForbiddenService;

    @PostMapping(value = "/getForbiddenList" ,produces = GlobalContext.PRODUCES)
    public String getForbiddenList(@RequestBody ApiForbiddenForm form){
        return respString(ResultCode.Success,ResultCode.SUCCESS,apiForbiddenService.list(form));
    }
    @PostMapping(value = "/getForbiddenByRole", produces = GlobalContext.PRODUCES)
    public String getForbiddenByRole(@RequestBody ApiLinkForm form){
        VerificationUtils.string("apiRoleId",form.getApiRoleId());
        return respString(ResultCode.Success,ResultCode.SUCCESS,apiForbiddenService.getForbiddenByRole(form));
    }

    @PostMapping(value = "/pushLink", produces = GlobalContext.PRODUCES)
    public String pushLink(@RequestBody ApiLinkForm form){
        VerificationUtils.string("apiRoleId",form.getApiRoleId());
        apiForbiddenService.pushLink(form);
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }

    @PostMapping(value = "/resetForbidden" , produces = GlobalContext.PRODUCES)
    public String resetForbidden(@RequestBody ApiDownLoadForm form){
        VerificationUtils.toStrAry("data",form.getData());
        apiForbiddenService.resetForbidden(form.getData());
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }
}
