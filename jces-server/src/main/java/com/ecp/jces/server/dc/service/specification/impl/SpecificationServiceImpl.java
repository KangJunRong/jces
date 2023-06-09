package com.ecp.jces.server.dc.service.specification.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.SpecificationForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.specification.SpecificationMapper;
import com.ecp.jces.server.dc.service.specification.SpecificationService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.SpecificationVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationServiceImpl.class);

    @Autowired
    private SpecificationMapper dao;

    @Override
    public List<SpecificationVo> findList(SpecificationForm specificationForm) throws FrameworkRuntimeException {
        return dao.findList(specificationForm);
    }

    @Override
    public Pagination<SpecificationVo> page(SpecificationForm specificationForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(specificationForm.getPage(), specificationForm.getPageCount());
        List<SpecificationVo> list = dao.findList(specificationForm);
        Pagination<SpecificationVo> pagination = new Pagination<>(specificationForm.getPage(), specificationForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public void add(SpecificationForm specificationForm) throws FrameworkRuntimeException {
        specificationForm.setPath(AesUtil2.decryptAES2(specificationForm.getPath()));

        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        specificationForm.setId(StrUtil.newGuid());
        specificationForm.setStatus(SpecificationVo.INIT_STATUS);
        try {
            specificationForm.setName(StrUtil.getFileNameFromUrl(specificationForm.getPath()));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
            specificationForm.setName("");
        }
        specificationForm.setDownloadTimes(0);
        specificationForm.setCreateUser(vo);
        specificationForm.setCreateDate(date);
        specificationForm.setUpdateUser(vo);
        specificationForm.setUpdateDate(date);
        specificationForm.setDelFlg(ResultCode.NOT_DEL);
        dao.insert(specificationForm);
    }

    @Override
    public void delete(SpecificationForm specificationForm) throws FrameworkRuntimeException {
        SpecificationVo specification = this.get(specificationForm);
        if(specification != null && specification.getDownloadTimes() > 0){
            throw new FrameworkRuntimeException(ResultCode.Fail, "此规范已经被下载过，不容许删除!");
        }
        specificationForm.setDelFlg(ResultCode.DEL);
        dao.delete(specificationForm);
    }

    @Override
    public void updateStatus(SpecificationForm specificationForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        specificationForm.setUpdateUser(vo);
        specificationForm.setUpdateDate(date);
        dao.updateStatus(specificationForm);
    }

    @Override
    public void downloadUpdate(SpecificationForm specificationForm) throws FrameworkRuntimeException {
        dao.downloadUpdate(specificationForm);
    }

    @Override
    public SpecificationVo get(SpecificationForm specificationForm) throws FrameworkRuntimeException {
        return dao.get(specificationForm);
    }
}
