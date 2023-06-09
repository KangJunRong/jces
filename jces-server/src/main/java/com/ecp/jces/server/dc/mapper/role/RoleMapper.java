package com.ecp.jces.server.dc.mapper.role;

import com.ecp.jces.form.RoleForm;
import com.ecp.jces.vo.RoleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    List<RoleVo> findList(RoleForm roleForm);

    void insert(RoleForm roleForm);

    void update(RoleForm roleForm);

    void delete(RoleForm roleForm);

    RoleVo getDeveloperRole();


}
