package com.ecp.jces.server.dc.mapper.sys;

import com.ecp.jces.form.SysConfigForm;
import com.ecp.jces.vo.SysConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysConfigMapper {

    List<SysConfigVo> findList(SysConfigForm sysConfigForm);

    void insert(SysConfigForm sysConfigForm);

    void update(SysConfigForm sysConfigForm);

    void delete(SysConfigForm sysConfigForm);

    SysConfigVo getByLabel(SysConfigForm sysConfigForm);

    List<SysConfigVo> selectAll();

    List<SysConfigVo> list(@Param("type")String type);

    String getValueByLabel(@Param("label")String label);
}
