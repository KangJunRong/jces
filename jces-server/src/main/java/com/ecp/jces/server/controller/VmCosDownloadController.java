package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.VmCosDownloadForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.cos.VmCosDownloadService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.VmCosDownloadVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cosDownload")
public class VmCosDownloadController extends Base {
    @Autowired
    private VmCosDownloadService vmCosDownloadService;

    @PostMapping(value = "/page",produces = GlobalContext.PRODUCES)
    public String page(@RequestBody VmCosDownloadForm cosForm){
        VerificationUtils.integer("page",cosForm.getPage());
        VerificationUtils.integer("pageCount",cosForm.getPageCount());
        Pagination<VmCosDownloadVo> pagination = vmCosDownloadService.page(cosForm);
        return respString(ResultCode.Success,ResultCode.SUCCESS,pagination);
    }
}
