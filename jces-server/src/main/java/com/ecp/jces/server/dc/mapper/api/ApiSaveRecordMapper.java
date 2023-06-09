package com.ecp.jces.server.dc.mapper.api;

import com.ecp.jces.form.ApiForbiddenSaveForm;
import com.ecp.jces.form.ApiSaveRecordForm;
import com.ecp.jces.vo.ApiForbiddenSaveVo;
import com.ecp.jces.vo.ApiSaveRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApiSaveRecordMapper {

    ApiSaveRecordVo findByTaskId(@Param("taskId") String taskId);

    int insert(ApiSaveRecordForm form);

    int delete(@Param("taskId") String taskId);
}