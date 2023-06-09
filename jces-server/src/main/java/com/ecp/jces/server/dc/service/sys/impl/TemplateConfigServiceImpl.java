package com.ecp.jces.server.dc.service.sys.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TemplateConfigForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.sys.TemplateConfigMapper;
import com.ecp.jces.server.dc.service.sys.TemplateConfigService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TemplateConfigVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TemplateConfigServiceImpl implements TemplateConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateConfigServiceImpl.class);

    @Autowired
    private TemplateConfigMapper dao;

    @Override
    public Pagination<TemplateConfigVo> page(TemplateConfigForm form) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(form.getPage(), form.getPageCount());
        List<TemplateConfigVo> list = dao.list(form);
        Pagination<TemplateConfigVo> pagination = new Pagination<>(form.getPage(), form.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public void add(TemplateConfigForm TemplateConfigForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        TemplateConfigForm.setId(StrUtil.newGuid());
        TemplateConfigForm.setCreateUser(vo.getAccount());
        TemplateConfigForm.setCreateDate(date);
        TemplateConfigForm.setUpdateUser(vo.getAccount());
        TemplateConfigForm.setUpdateDate(date);
        TemplateConfigForm.setDelFlg(ResultCode.NOT_DEL);
        dao.insert(TemplateConfigForm);
    }

    @Override
    public void update(TemplateConfigForm form) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        form.setUpdateUser(vo.getAccount());
        form.setUpdateDate(date);
        dao.update(form);
    }

    @Override
    public void delete(TemplateConfigForm form) throws FrameworkRuntimeException {
        dao.delete(form.getId());
    }

    @Override
    public TemplateConfigVo getById(TemplateConfigForm form) throws FrameworkRuntimeException {
        return dao.findById(form.getId());
    }

    @Override
    public List<TemplateConfigVo> list(TemplateConfigForm form) throws FrameworkRuntimeException {
        return dao.list(form);
    }
}
