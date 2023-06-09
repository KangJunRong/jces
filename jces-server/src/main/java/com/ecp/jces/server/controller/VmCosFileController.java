package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.VmCosFileForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.cos.VmCosFileService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.VmCosFileVo;
import com.ecp.jces.core.utils.pagination.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cosFile")
public class VmCosFileController extends Base {

    @Autowired
    private VmCosFileService vmCosFileService;

    @PostMapping(value = "/page",produces = GlobalContext.PRODUCES)
    public String page(@RequestBody VmCosFileForm vmCosFileForm) throws FrameworkRuntimeException{
        VerificationUtils.integer("page",  vmCosFileForm.getPage());
        VerificationUtils.integer("pageCount", vmCosFileForm.getPageCount());
        VerificationUtils.string("cosId",vmCosFileForm.getCosId());
        Pagination<VmCosFileVo> pagination = vmCosFileService.page(vmCosFileForm);
        return respString(ResultCode.Success,ResultCode.SUCCESS, pagination);
    }

    // 修改del_flg为1
    @PostMapping(value = "/delVmCosFile",produces = GlobalContext.PRODUCES)
    public String delVmCosFile(@RequestBody VmCosFileForm vmCosFileForm){
        VerificationUtils.string("id",vmCosFileForm.getId());
        vmCosFileService.del(vmCosFileForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    // 完全删除
    @PostMapping(value = "/delVmCosFileData", produces = GlobalContext.PRODUCES)
    public String delVmCosFileData(@RequestBody VmCosFileForm vmCosFileForm) {
        VerificationUtils.string("cosId", vmCosFileForm.getCosId());
        vmCosFileService.delete(vmCosFileForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody VmCosFileForm vmCosFileForm){
/*        VerificationUtils.string("no",vmCosFileForm.getFileName());*/
        VerificationUtils.string("cosId",vmCosFileForm.getCosId());
        VerificationUtils.string("fileId",vmCosFileForm.getFileId(),false,1024);
        VerificationUtils.string("fileName",vmCosFileForm.getFileName());
        VerificationUtils.string("size",vmCosFileForm.getSize(),false,16);
        vmCosFileService.add(vmCosFileForm);
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }
}
