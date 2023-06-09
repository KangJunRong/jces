package com.ecp.jces.server.dc.service.sys;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.IdeLogMsgForm;
import com.ecp.jces.vo.IdeLogMsgVo;

import java.util.List;

public interface IdeLogMsgService {

    Pagination<IdeLogMsgVo> page(IdeLogMsgForm ideLogMsgForm) throws FrameworkRuntimeException;

    IdeLogMsgVo findById(IdeLogMsgForm ideLogMsgForm) throws FrameworkRuntimeException;

    void add(IdeLogMsgForm ideLogMsgForm) throws FrameworkRuntimeException;

    void adds(List<IdeLogMsgForm> list,String ip) throws FrameworkRuntimeException;

}
