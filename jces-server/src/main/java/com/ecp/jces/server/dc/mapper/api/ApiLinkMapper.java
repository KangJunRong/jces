package com.ecp.jces.server.dc.mapper.api;

import com.ecp.jces.form.ApiLinkForm;
import com.ecp.jces.vo.ApiLinkVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiLinkMapper {
    List<ApiLinkVo> getForbiddenByRole(ApiLinkForm form);
    void getNew(String id);
    void pushLink(ApiLinkForm form);
}
