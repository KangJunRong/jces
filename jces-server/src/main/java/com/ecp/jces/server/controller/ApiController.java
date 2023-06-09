package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.ApiRoleForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.api.ApiService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.ApiRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController extends Base{
    @Autowired
    private ApiService apiService;

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody ApiRoleForm form){
        VerificationUtils.integer("page",form.getPage());
        VerificationUtils.integer("pageCount",form.getPageCount());
        Pagination<ApiRoleVo> pagination = apiService.page(form);
        return respString(ResultCode.Success,ResultCode.SUCCESS,pagination);
    }

    @PostMapping(value = "/getList", produces = GlobalContext.PRODUCES)
    public String getList(@RequestBody ApiRoleForm form){
        return respString(ResultCode.Success,ResultCode.SUCCESS,apiService.list(form));
    }
    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody ApiRoleForm form){
        VerificationUtils.string("name",form.getName(),false,36);
        VerificationUtils.string("remark", form.getRemark(),true,128);
        return respString(ResultCode.Success,ResultCode.SUCCESS,apiService.add(form));
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody ApiRoleForm form){
        VerificationUtils.string("id",form.getId(),false,36);
        VerificationUtils.string("name",form.getName(),false,36);
        VerificationUtils.string("remark", form.getRemark(),true,128);
        apiService.edit(form);
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }
    @PostMapping(value = "/findById",produces = GlobalContext.PRODUCES)
    public String findById(@RequestBody ApiRoleForm form){
        VerificationUtils.string("id",form.getId());
        return respString(ResultCode.Success,ResultCode.SUCCESS,apiService.findById(form));
    }

    @PostMapping(value = "/del",produces = GlobalContext.PRODUCES)
    public String del(@RequestBody ApiRoleForm form){
        VerificationUtils.string("id",form.getId());
        apiService.del(form);
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }
}
