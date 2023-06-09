package com.ecp.jces.server.dc.service.api;

import com.ecp.jces.form.ApiForbiddenForm;
import com.ecp.jces.form.ApiLinkForm;
import com.ecp.jces.vo.ApiForbiddenVo;
import com.ecp.jces.vo.ApiLinkVo;

import java.util.List;

public interface ApiForbiddenService {
    List<ApiLinkVo> getForbiddenByRole(ApiLinkForm form);
    void pushLink(ApiLinkForm form);
    void resetForbidden(String[][] data);
    List<ApiForbiddenVo> list(ApiForbiddenForm form);
}
