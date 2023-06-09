package com.ecp.jces.server.dc.service.applet.impl;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.AppletVersionForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.applet.AppletVersionMapper;
import com.ecp.jces.server.dc.service.applet.AppletVersionService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.AppletVersionVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class AppletVersionServiceImpl implements AppletVersionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppletVersionServiceImpl.class);

    @Autowired
    private AppletVersionMapper appletVersionMapper;


    @Override
    public AppletVersionVo findById(String appletVersionId) throws FrameworkRuntimeException {
        return appletVersionMapper.findById(appletVersionId);
    }

    @Override
    public List<AppletVersionVo> list(AppletVersionForm appletVersionForm) throws FrameworkRuntimeException {

        appletVersionForm.setDelFlg(ResultCode.NOT_DEL);
        return appletVersionMapper.list(appletVersionForm);
    }

    @Override
    public Pagination<AppletVersionVo> page(AppletVersionForm form) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(form.getPage(), form.getPageCount());
        form.setDelFlg(ResultCode.NOT_DEL);
        List<AppletVersionVo> list = appletVersionMapper.list(form);
        Pagination<AppletVersionVo> pagination = new Pagination<>(form.getPage(), form.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public void add(AppletVersionForm appletVersionForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        appletVersionForm.setStatus(ConstantCode.APPLET_STATUS_NOT_COMMIT);
        appletVersionForm.setId(StrUtil.newGuid());
        appletVersionForm.setCreateUser(vo.getId());
        appletVersionForm.setCreateDate(date);
        appletVersionForm.setUpdateUser(vo.getId());
        appletVersionForm.setUpdateDate(date);
        appletVersionForm.setDelFlg(ResultCode.NOT_DEL);
        appletVersionForm.setExamine(ConstantCode.EXAMINE_STATUS_NOT_COMMIT);
        appletVersionMapper.add(appletVersionForm);
    }

    @Override
    public void edit(AppletVersionForm appletVersionForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        appletVersionForm.setUpdateUser(vo.getId());
        appletVersionForm.setUpdateDate(date);
        appletVersionMapper.edit(appletVersionForm);
    }

    @Override
    public void del(AppletVersionForm appletVersionForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        appletVersionForm.setUpdateUser(vo.getId());
        appletVersionForm.setUpdateDate(date);
        appletVersionForm.setDelFlg(ResultCode.DEL);
        appletVersionMapper.edit(appletVersionForm);
    }

    @Override
    public Integer getCapLastVersion(String appletId) throws FrameworkRuntimeException {
        return appletVersionMapper.getCapLastVersion(appletId);
    }

    @Override
    public List<Map<String, Object>> appletVersionCreateCount(AppletVersionForm form) throws FrameworkRuntimeException {
        return appletVersionMapper.appletVersionCreateCount(form);
    }
}
