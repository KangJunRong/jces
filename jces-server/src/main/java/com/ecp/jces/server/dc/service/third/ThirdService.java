package com.ecp.jces.server.dc.service.third;


import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.AppletForm;
import com.ecp.jces.form.UserForm;
import com.ecp.jces.form.extra.SingleLoginForm;
import com.ecp.jces.form.extra.SyncAccountForm;

import java.util.Map;


public interface ThirdService {

    void commitCap(AppletForm form) throws FrameworkRuntimeException;

    String getVoucher(UserForm userForm) throws FrameworkRuntimeException;

    Map<String, Object> singleLogin(SingleLoginForm form) throws FrameworkRuntimeException;

    void synchronizeAccount(SyncAccountForm form) throws FrameworkRuntimeException;
}
