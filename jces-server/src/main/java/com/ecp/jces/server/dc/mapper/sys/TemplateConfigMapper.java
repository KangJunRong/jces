package com.ecp.jces.server.dc.mapper.sys;
import com.ecp.jces.form.TemplateConfigForm;
import com.ecp.jces.vo.TemplateConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TemplateConfigMapper {

    int delete(@Param("id") String id);

    int insert(TemplateConfigForm form);

    TemplateConfigVo findById(@Param("id") String id);

    TemplateConfigVo findByName(@Param("name") String name);

    int update(TemplateConfigForm form);

    List<TemplateConfigVo> list(TemplateConfigForm form);
}