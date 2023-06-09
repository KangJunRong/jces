package com.ecp.jces.server.dc.service.role.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.RoleForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.role.RoleMapper;
import com.ecp.jces.server.dc.service.role.RoleService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.RoleVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleMapper dao;


    @Override
    public List<RoleVo> findList(RoleForm roleForm) throws FrameworkRuntimeException {
        return dao.findList(roleForm);
    }

    @Override
    public Pagination<RoleVo> page(RoleForm roleForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(roleForm.getPage(), roleForm.getPageCount());
        List<RoleVo> list = dao.findList(roleForm);
        Pagination<RoleVo> pagination = new Pagination<>(roleForm.getPage(), roleForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void add(RoleForm roleForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        roleForm.setId(StrUtil.newGuid());
        roleForm.setCreateUser(vo);
        roleForm.setCreateDate(date);
        roleForm.setUpdateUser(vo);
        roleForm.setUpdateDate(date);
        roleForm.setDelFlg(ResultCode.NOT_DEL);
        dao.insert(roleForm);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void update(RoleForm roleForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        roleForm.setUpdateUser(vo);
        roleForm.setUpdateDate(date);
        dao.update(roleForm);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void delete(RoleForm roleForm) throws FrameworkRuntimeException {
        roleForm.setDelFlg(ResultCode.DEL);
        dao.delete(roleForm);
    }
}
