package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.AppletForm;
import com.ecp.jces.form.AppletVersionForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.applet.AppletVersionService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.AppletVersionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appletVersion")
public class AppletVersionController extends Base {
    @Autowired
    private AppletVersionService appletVersionService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody AppletVersionForm form) {
        VerificationUtils.string("name", form.getName());
        appletVersionService.add(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody AppletVersionForm form) {
        VerificationUtils.string("id", form.getId());
        appletVersionService.edit(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody AppletVersionForm form) {
        VerificationUtils.string("id", form.getId());
        appletVersionService.del(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody AppletVersionForm form) {
        VerificationUtils.integer("page", form.getPage());
        VerificationUtils.integer("pageCount", form.getPageCount());
        Pagination<AppletVersionVo> pagination = appletVersionService.page(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/get", produces = GlobalContext.PRODUCES)
    public String get(@RequestBody AppletVersionForm form) {
        VerificationUtils.string("id", form.getId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, appletVersionService.findById(form.getId()));
    }

    @PostMapping(value = "/getCapLastVersion", produces = GlobalContext.PRODUCES)
    public String getCapLastVersion(@RequestBody AppletVersionForm form) {
        VerificationUtils.string("appletId", form.getAppletId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, appletVersionService.getCapLastVersion(form.getAppletId()));
    }

    @PostMapping(value = "/capCreateCount", produces = GlobalContext.PRODUCES)
    public String capCreateCount(@RequestBody AppletVersionForm form) {
        List<Map<String, Object>> data = appletVersionService.appletVersionCreateCount(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }


}
