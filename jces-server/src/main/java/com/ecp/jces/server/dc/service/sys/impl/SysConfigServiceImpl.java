package com.ecp.jces.server.dc.service.sys.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.SysConfigForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.sys.SysConfigMapper;
import com.ecp.jces.server.dc.service.sys.SysConfigService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.SysConfigVo;
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
public class SysConfigServiceImpl implements SysConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysConfigServiceImpl.class);

    @Autowired
    private SysConfigMapper dao;

    @Override
    public List<SysConfigVo> findList(SysConfigForm sysConfigForm) throws FrameworkRuntimeException {
        return dao.findList(sysConfigForm);
    }

    @Override
    public Pagination<SysConfigVo> page(SysConfigForm sysConfigForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(sysConfigForm.getPage(), sysConfigForm.getPageCount());
        List<SysConfigVo> list = dao.findList(sysConfigForm);
        Pagination<SysConfigVo> pagination = new Pagination<>(sysConfigForm.getPage(), sysConfigForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public void add(SysConfigForm sysConfigForm) throws FrameworkRuntimeException {
        VerificationUtils.string("label", sysConfigForm.getLabel(), false, 512);
        VerificationUtils.string("value", sysConfigForm.getValue(), false, 512);
        //校验label唯一
        SysConfigVo oldSysConfig = dao.getByLabel(sysConfigForm);
        if(oldSysConfig != null){
            throw new FrameworkRuntimeException(ResultCode.Fail, "键已经存在!");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        sysConfigForm.setId(StrUtil.newGuid());
        sysConfigForm.setCreateUser(vo);
        sysConfigForm.setCreateDate(date);
        sysConfigForm.setUpdateUser(vo);
        sysConfigForm.setUpdateDate(date);
        sysConfigForm.setDelFlg(ResultCode.NOT_DEL);
        dao.insert(sysConfigForm);
    }

    @Override
    public void update(SysConfigForm sysConfigForm) throws FrameworkRuntimeException {
        VerificationUtils.string("label", sysConfigForm.getLabel(), false, 512);
        VerificationUtils.string("value", sysConfigForm.getValue(), false, 512);
        //校验label唯一
        SysConfigVo oldSysConfig = dao.getByLabel(sysConfigForm);
        if(oldSysConfig != null && !oldSysConfig.getId().equals(sysConfigForm.getId())){
            throw new FrameworkRuntimeException(ResultCode.Fail, "键已经存在!");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        sysConfigForm.setUpdateUser(vo);
        sysConfigForm.setUpdateDate(date);
        dao.update(sysConfigForm);

    }

    @Override
    public void delete(SysConfigForm sysConfigForm) throws FrameworkRuntimeException {
        sysConfigForm.setDelFlg(ResultCode.DEL);
        dao.delete(sysConfigForm);
    }

    @Override
    public SysConfigVo getByLabel(SysConfigForm sysConfigForm) throws FrameworkRuntimeException {
        if(sysConfigForm != null && StringUtils.isNotBlank(sysConfigForm.getLabel())){
            return dao.getByLabel(sysConfigForm);
        }
        return null;
    }
}
