package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.ApiLogForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.api.ApiLogService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.ApiLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiLog")
public class ApiLogController extends Base {
    @Autowired
    private ApiLogService apiLogService;
    @PostMapping(value = "/page",produces = GlobalContext.PRODUCES)
    public String page(@RequestBody ApiLogForm form){
        VerificationUtils.integer("page",form.getPage());
        VerificationUtils.integer("pageCount",form.getPageCount());
        Pagination<ApiLogVo> pagination = apiLogService.page(form);
        return respString(ResultCode.Success,ResultCode.SUCCESS,pagination);
    }
}
