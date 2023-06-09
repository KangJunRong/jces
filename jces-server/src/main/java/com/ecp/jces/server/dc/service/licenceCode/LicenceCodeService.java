package com.ecp.jces.server.dc.service.licenceCode;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.LicenceCodeForm;
import com.ecp.jces.form.UserForm;
import com.ecp.jces.vo.LicenceCodeVo;
import com.ecp.jces.vo.UserVo;

import java.util.Date;
import java.util.List;


public interface LicenceCodeService {


    Pagination<LicenceCodeVo> page(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException;

    Pagination<UserVo> applyManagePage(UserForm userForm) throws FrameworkRuntimeException;

    void add(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException;

    void edit(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException;

    void cancel(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException;

    void audit(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException;

    void reject(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException;

    int appliedLicenceCodeCount(LicenceCodeForm licenceCodeForm);

    Date getLicenceCodeExpiryDate();

    void updateLicenceCodeApiRoleId(LicenceCodeForm licenceCodeForm);

    LicenceCodeVo findById(String id);

    String apiResetLicenceCode(LicenceCodeForm licenceCode, List<String> list);

    String bindResetLicenceCode(LicenceCodeForm licenceCode);
}
