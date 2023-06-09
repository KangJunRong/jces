package com.ecp.jces.server.dc.mapper.api;

import com.ecp.jces.form.ApiForbiddenDefaultForm;
import com.ecp.jces.vo.ApiForbiddenDefaultVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApiForbiddenDefaultMapper {
    
    int deleteAll();

    int insert(@Param("list") List<ApiForbiddenDefaultForm> list);

    List<ApiForbiddenDefaultVo> list();
}