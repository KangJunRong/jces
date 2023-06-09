package com.ecp.jces.server.dc.service.api;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.ApiRoleForm;
import com.ecp.jces.vo.ApiRoleVo;

import java.util.List;

public interface ApiService {
    Pagination<ApiRoleVo> page(ApiRoleForm form);

    String add(ApiRoleForm form);

    void edit(ApiRoleForm form);

    ApiRoleVo findById(ApiRoleForm form);

    void del(ApiRoleForm form);

    List<ApiRoleVo> list(ApiRoleForm form);
}
