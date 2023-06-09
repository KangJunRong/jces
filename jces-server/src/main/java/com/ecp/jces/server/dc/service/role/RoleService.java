package com.ecp.jces.server.dc.service.role;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.RoleForm;
import com.ecp.jces.vo.RoleVo;

import java.util.List;

public interface RoleService {

    List<RoleVo> findList(RoleForm roleForm) throws FrameworkRuntimeException;

    Pagination<RoleVo> page(RoleForm roleForm) throws FrameworkRuntimeException;

    void add(RoleForm roleForm) throws FrameworkRuntimeException;

    void update(RoleForm roleForm) throws FrameworkRuntimeException;

    void delete(RoleForm roleForm) throws FrameworkRuntimeException;

}
