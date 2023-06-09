package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.LicenceCodeForm;
import com.ecp.jces.form.UserForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.licenceCode.LicenceCodeService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.LicenceCodeVo;
import com.ecp.jces.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/licenceCode")
public class LicenceCodeController extends Base {
    @Autowired
    private LicenceCodeService licenceCodeService;

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody LicenceCodeForm licenceCodeForm) {
        VerificationUtils.string("machineCode", licenceCodeForm.getMachineCode(), false, 64);
        licenceCodeService.add(licenceCodeForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody LicenceCodeForm licenceCodeForm) {
        VerificationUtils.string("id", licenceCodeForm.getId(), false, 36);
        VerificationUtils.string("machineCode", licenceCodeForm.getMachineCode(), false, 64);
        licenceCodeService.edit(licenceCodeForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/cancel", produces = GlobalContext.PRODUCES)
    public String cancel(@RequestBody LicenceCodeForm licenceCodeForm) {
        VerificationUtils.string("id", licenceCodeForm.getId(), false, 36);
        licenceCodeService.cancel(licenceCodeForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }
    @PostMapping(value = "/audit", produces = GlobalContext.PRODUCES)
    public String audit(@RequestBody LicenceCodeForm licenceCodeForm) {
        VerificationUtils.string("id", licenceCodeForm.getId(), false, 36);
        VerificationUtils.string("machineCode", licenceCodeForm.getMachineCode(), false, 64);
        VerificationUtils.date("expiryDate", licenceCodeForm.getExpiryDate(), false);
        licenceCodeService.audit(licenceCodeForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/reject", produces = GlobalContext.PRODUCES)
    public String reject(@RequestBody LicenceCodeForm licenceCodeForm) {
        VerificationUtils.string("id", licenceCodeForm.getId(), false, 36);
        VerificationUtils.string("rejectDesc", licenceCodeForm.getRejectDesc(), false, 500);
        licenceCodeService.reject(licenceCodeForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }
    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody LicenceCodeForm licenceCodeForm) {
        VerificationUtils.integer("page", licenceCodeForm.getPage());
        VerificationUtils.integer("pageCount", licenceCodeForm.getPageCount());
        Pagination<LicenceCodeVo> pagination = licenceCodeService.page(licenceCodeForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/applyManagePage", produces = GlobalContext.PRODUCES)
    public String applyManagePage(@RequestBody UserForm userForm) {
        VerificationUtils.integer("page", userForm.getPage());
        VerificationUtils.integer("pageCount", userForm.getPageCount());
        Pagination<UserVo> pagination = licenceCodeService.applyManagePage(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/appliedLicenceCodeCount", produces = GlobalContext.PRODUCES)
    public String appliedLicenceCodeCount(@RequestBody LicenceCodeForm licenceCodeForm) {
        return respString(ResultCode.Success, ResultCode.SUCCESS, licenceCodeService.appliedLicenceCodeCount(licenceCodeForm));
    }

    @PostMapping(value = "/getLicenceCodeExpiryDate", produces = GlobalContext.PRODUCES)
    public String getLicenceCodeExpiryDate() {
        return respString(ResultCode.Success, ResultCode.SUCCESS, licenceCodeService.getLicenceCodeExpiryDate());
    }

    @PostMapping(value = "/updateLicenceCodeApiRoleId", produces = GlobalContext.PRODUCES)
    public String updateLicenceCodeApiRoleId(@RequestBody LicenceCodeForm form){
        VerificationUtils.string("id",form.getId());
        VerificationUtils.string("apiRoleId",form.getApiRoleId());
        licenceCodeService.updateLicenceCodeApiRoleId(form);
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }

}
