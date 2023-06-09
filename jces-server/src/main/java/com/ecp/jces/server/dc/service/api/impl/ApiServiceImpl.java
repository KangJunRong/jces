package com.ecp.jces.server.dc.service.api.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.ApiRoleForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.api.ApiRoleMapper;
import com.ecp.jces.server.dc.service.api.ApiService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.ApiRoleVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ApiServiceImpl implements ApiService {
    @Autowired
    private ApiRoleMapper apiRoleMapper;

    public Pagination<ApiRoleVo> page(ApiRoleForm form){
        Page<Object> pageHelper = PageHelper.startPage(form.getPage(),form.getPageCount());
        List<ApiRoleVo> list = apiRoleMapper.list(form);
        Pagination<ApiRoleVo> pagination = new Pagination<>(form.getPage(),form.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    public List<ApiRoleVo> list(ApiRoleForm form){
        return apiRoleMapper.list(form);
    }

    public String add(ApiRoleForm form){
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        form.setId(StrUtil.newGuid());
        form.setCreateUser(vo.getName());
        form.setDelFlg(ResultCode.NOT_DEL);
        form.setCreateDate(date);
        form.setUpdateDate(date);
        form.setUpdateUser(vo.getName());
        apiRoleMapper.add(form);
        return form.getId();
    }

    public void edit(ApiRoleForm form){
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        form.setUpdateDate(date);
        form.setUpdateUser(vo.getName());
        apiRoleMapper.edit(form);
    }

    public ApiRoleVo findById(ApiRoleForm form){
        return apiRoleMapper.findById(form);
    }

    public void del(ApiRoleForm form){
        apiRoleMapper.del(form);
    }
}
