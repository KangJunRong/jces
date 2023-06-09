package com.ecp.jces.server.dc.mapper.api;

import com.ecp.jces.form.ApiForbiddenSaveForm;
import com.ecp.jces.vo.ApiForbiddenSaveVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApiForbiddenSaveMapper {
    
    int deleteAll();

    int insert(@Param("list") List<ApiForbiddenSaveForm> list);

    List<ApiForbiddenSaveVo> list();
}