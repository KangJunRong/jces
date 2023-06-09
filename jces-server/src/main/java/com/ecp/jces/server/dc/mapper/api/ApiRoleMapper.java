package com.ecp.jces.server.dc.mapper.api;

import com.ecp.jces.form.ApiRoleForm;
import com.ecp.jces.vo.ApiRoleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiRoleMapper {
    List<ApiRoleVo> list(ApiRoleForm form);
    void add(ApiRoleForm form);
    void edit(ApiRoleForm form);
    ApiRoleVo findById(ApiRoleForm form);
    void del(ApiRoleForm form);
}
