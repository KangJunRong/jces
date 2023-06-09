package com.ecp.jces.server.dc.mapper.api;

import com.ecp.jces.form.ApiLogForm;
import com.ecp.jces.vo.ApiLogVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiLogMapper {
    List<ApiLogVo> list(ApiLogForm form);
    int insert(ApiLogForm form);
}
