package com.ecp.jces.server.dc.service.sys.impl;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.IdeLogMsgForm;
import com.ecp.jces.server.dc.mapper.sys.IdeLogMsgMapper;
import com.ecp.jces.server.dc.service.sys.IdeLogMsgService;
import com.ecp.jces.server.util.DateUtil;
import com.ecp.jces.vo.IdeLogMsgVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class IdeLogMsgServiceImpl implements IdeLogMsgService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdeLogMsgServiceImpl.class);

    @Autowired
    private IdeLogMsgMapper ideLogMsgMapper;


    @Override
    public Pagination<IdeLogMsgVo> page(IdeLogMsgForm ideLogMsgForm) throws FrameworkRuntimeException {
        if (ideLogMsgForm.getUploadTime() != null) {
            String date = DateUtil.formatsSort(ideLogMsgForm.getUploadTime());
            ideLogMsgForm.setStartTime(date + " 00:00:00");
            ideLogMsgForm.setEndTime(date + " 23:59:59");
        }

        Page<Object> pageHelper = PageHelper.startPage(ideLogMsgForm.getPage(), ideLogMsgForm.getPageCount());
        List<IdeLogMsgVo> list = ideLogMsgMapper.list(ideLogMsgForm);
        Pagination<IdeLogMsgVo> pagination = new Pagination<>(ideLogMsgForm.getPage(), ideLogMsgForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public IdeLogMsgVo findById(IdeLogMsgForm ideLogMsgForm) throws FrameworkRuntimeException {
        return ideLogMsgMapper.findById(ideLogMsgForm.getId());
    }

    @Override
    public void add(IdeLogMsgForm ideLogMsgForm) throws FrameworkRuntimeException {
        ideLogMsgForm.setUploadTime(new Date());
        ideLogMsgMapper.insert(ideLogMsgForm);
    }

    @Override
    public void adds(List<IdeLogMsgForm> list, String ip) throws FrameworkRuntimeException {
        ideLogMsgMapper.adds(list, ip, new Date());
    }
}
