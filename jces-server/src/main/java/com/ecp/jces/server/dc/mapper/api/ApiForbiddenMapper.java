package com.ecp.jces.server.dc.mapper.api;

import com.ecp.jces.form.ApiForbiddenForm;
import com.ecp.jces.vo.ApiForbiddenVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApiForbiddenMapper {
    List<ApiForbiddenVo> list(ApiForbiddenForm form);
    void reset();
    void pushAll(List<ApiForbiddenForm> list);
    List<ApiForbiddenVo> getForbiddenByRoleId(@Param("id")String id);
    List<ApiForbiddenVo> getForbiddenByListId(List<String> list);

    List<ApiForbiddenVo> findByUserId(@Param("id") String id);
}
