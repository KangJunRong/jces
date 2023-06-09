package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.VmCosForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.cos.VmCosFileService;
import com.ecp.jces.server.dc.service.cos.VmCosService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.VmCosVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cos")
public class VmCosController extends Base {
    @Autowired
    private VmCosService vmCosService;
    @Autowired
    private VmCosFileService vmCosFileService;

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody VmCosForm cosForm) {
        VerificationUtils.string("id", cosForm.getId());
        VerificationUtils.string("versionNo", cosForm.getVersionNo(), false, 32);
        VerificationUtils.string("remark", cosForm.getRemark(), true, 128);
        vmCosService.edit(cosForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody VmCosForm cosForm) {
        VerificationUtils.integer("page", cosForm.getPage());
        VerificationUtils.integer("pageCount", cosForm.getPageCount());
        Pagination<VmCosVo> pagination = vmCosService.page(cosForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/list", produces = GlobalContext.PRODUCES)
    public String list(@RequestBody VmCosForm form) {
        return respString(ResultCode.Success, ResultCode.SUCCESS, vmCosService.list(form));
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody VmCosForm cosForm) {
        VerificationUtils.string("id", cosForm.getId());
        vmCosService.del(cosForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/updateStatus", produces = GlobalContext.PRODUCES)
    public String updateStatus(@RequestBody VmCosForm vmCosForm) {
        VerificationUtils.string("id", vmCosForm.getId());
        VerificationUtils.integer("status", vmCosForm.getStatus());
        vmCosService.updateStatus(vmCosForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody VmCosForm form) {
        VerificationUtils.string("versionNo", form.getVersionNo());
        VerificationUtils.obj("fileForm", form.getFileForm());
        vmCosService.add(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @GetMapping(value = "/getCosChildVersion", produces = GlobalContext.PRODUCES)
    public String getCosChildVersion() {

        return respString(ResultCode.Success, ResultCode.SUCCESS, vmCosFileService.getCosChildVersion());
    }
}
