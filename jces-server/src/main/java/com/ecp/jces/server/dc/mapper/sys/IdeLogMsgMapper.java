package com.ecp.jces.server.dc.mapper.sys;

import com.ecp.jces.form.IdeLogMsgForm;
import com.ecp.jces.vo.IdeLogMsgVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface IdeLogMsgMapper {


    void insert(IdeLogMsgForm form);

    void adds(@Param("list") List<IdeLogMsgForm> list,
              @Param("ip") String ip, @Param("uploadTime") Date uploadTime);

    void delete(@Param("id") Long id);

    IdeLogMsgVo findById(@Param("id") Long id);

    List<IdeLogMsgVo> list(IdeLogMsgForm form);

}
