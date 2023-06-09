package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.SpecificationForm;
import com.ecp.jces.form.TestScriptForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.specification.SpecificationService;
import com.ecp.jces.server.dc.service.sysScript.TestScriptService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.SpecificationVo;
import com.ecp.jces.vo.TestScriptVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/specification")
public class SpecificationController extends Base {
    @Autowired
    private SpecificationService specificationService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody SpecificationForm form) {
        VerificationUtils.string("path", form.getPath(), false, 1024);
        specificationService.add(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }


    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody SpecificationForm form) {
        VerificationUtils.string("id", form.getId());
        specificationService.delete(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody SpecificationForm form) {
        VerificationUtils.integer("page", form.getPage());
        VerificationUtils.integer("pageCount", form.getPageCount());
        Pagination<SpecificationVo> pagination = specificationService.page(form);
        if (pagination.getData() != null && pagination.getData().size() > 0) {
            for(SpecificationVo vo : pagination.getData()){
                if(StrUtil.isNotBlank(vo.getPath())){
                    vo.setPath(AesUtil2.encryptData(vo.getPath()));
                }
            }
        }
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/updateStatus", produces = GlobalContext.PRODUCES)
    public String updateStatus(@RequestBody SpecificationForm form) {
        VerificationUtils.string("id", form.getId());
        specificationService.updateStatus(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/downloadUpdate", produces = GlobalContext.PRODUCES)
    public String downloadUpdate(@RequestBody SpecificationForm form) {
        VerificationUtils.string("id", form.getId());
        specificationService.downloadUpdate(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }


}
