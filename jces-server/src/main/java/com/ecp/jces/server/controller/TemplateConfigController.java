package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.form.TemplateConfigForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.mapper.sys.TemplateConfigMapper;
import com.ecp.jces.server.dc.service.sys.SysConfigService;
import com.ecp.jces.server.dc.service.sys.TemplateConfigService;
import com.ecp.jces.server.util.VerificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: jces-engine
 * @description:
 * @author: KJR
 * @create: 2023-03-15 14:23
 **/
@RestController
@RequestMapping("/templateConfig")
public class TemplateConfigController extends Base{

    @Autowired
    private TemplateConfigService service;

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody TemplateConfigForm form) {
        VerificationUtils.integer("page", form.getPage());
        VerificationUtils.integer("pageCount", form.getPageCount());
        return respString(ResultCode.Success, ResultCode.SUCCESS, service.page(form));
    }

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody TemplateConfigForm form) {
        VerificationUtils.string("name", form.getName(),false,32);
        VerificationUtils.string("collects", form.getCollects(),false,128);
        VerificationUtils.string("remark", form.getCollects(),true,128);
        service.add(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/findById", produces = GlobalContext.PRODUCES)
    public String findById(@RequestBody TemplateConfigForm form) {
        VerificationUtils.string("id", form.getId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, service.getById(form));
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody TemplateConfigForm form) {
        VerificationUtils.string("id", form.getId());
        VerificationUtils.string("name", form.getName(),false,32);
        VerificationUtils.string("collects", form.getCollects(),false,128);
        VerificationUtils.string("remark", form.getCollects(),true,128);
        service.update(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody TemplateConfigForm form) {
        VerificationUtils.string("id", form.getId());
        service.delete(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/list", produces = GlobalContext.PRODUCES)
    public String list(@RequestBody TemplateConfigForm form) {
        return respString(ResultCode.Success, ResultCode.SUCCESS, service.list(form));
    }
}
