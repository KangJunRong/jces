package com.ecp.jces.server.dc.service.terminal.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestCardForm;
import com.ecp.jces.form.TestCardManufacturerForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.terminal.TestCardManufacturerMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestCardMapper;
import com.ecp.jces.server.dc.service.terminal.TestCardManufacturerService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.TestCardManufacturerVo;
import com.ecp.jces.vo.TestCardVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TestCardManufacturerServiceImpl implements TestCardManufacturerService {

    private static final Logger logger = LoggerFactory.getLogger(TestCardManufacturerServiceImpl.class);

    @Autowired
    private TestCardManufacturerMapper dao;
    @Autowired
    private TestCardMapper testCardMapper;

    @Override
    public List<TestCardManufacturerVo> findList(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException {
        return dao.findList(TestCardManufacturerForm);
    }

    @Override
    public Pagination<TestCardManufacturerVo> page(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(TestCardManufacturerForm.getPage(), TestCardManufacturerForm.getPageCount());
        List<TestCardManufacturerVo> list = dao.findList(TestCardManufacturerForm);
        Pagination<TestCardManufacturerVo> pagination = new Pagination<>(TestCardManufacturerForm.getPage(), TestCardManufacturerForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void add(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException {

        //验证厂商名称唯一性
        TestCardManufacturerVo oldCardManufacturerByName = dao.getByName(TestCardManufacturerForm);
        if(oldCardManufacturerByName != null){
            throw new FrameworkRuntimeException(ResultCode.Fail, "厂商名称已经存在！");
        }
        //验证code唯一性
        TestCardManufacturerVo oldCardManufacturer = dao.getByCode(TestCardManufacturerForm);
        if(oldCardManufacturer != null){
            throw new FrameworkRuntimeException(ResultCode.Fail, "厂商编码已经存在！");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        TestCardManufacturerForm.setId(StrUtil.newGuid());
        TestCardManufacturerForm.setCreateUser(vo);
        TestCardManufacturerForm.setCreateDate(date);
        TestCardManufacturerForm.setUpdateUser(vo);
        TestCardManufacturerForm.setUpdateDate(date);
        TestCardManufacturerForm.setDelFlg(ResultCode.NOT_DEL);
        dao.insert(TestCardManufacturerForm);
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void update(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException {
        //验证name唯一性
        TestCardManufacturerVo oldCardManufacturerByName = dao.getByName(TestCardManufacturerForm);
        if(oldCardManufacturerByName != null && !oldCardManufacturerByName.getId().equals(TestCardManufacturerForm.getId())){
            throw new FrameworkRuntimeException(ResultCode.Fail, "厂商名称已经存在！");
        }

        //验证code唯一性
        TestCardManufacturerVo oldCardManufacturer = dao.getByCode(TestCardManufacturerForm);
        if(oldCardManufacturer != null && !oldCardManufacturer.getId().equals(TestCardManufacturerForm.getId())){
            throw new FrameworkRuntimeException(ResultCode.Fail, "厂商编码已经存在！");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        TestCardManufacturerForm.setUpdateUser(vo);
        TestCardManufacturerForm.setUpdateDate(date);
        dao.update(TestCardManufacturerForm);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void delete(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException {
        TestCardForm cardForm = new TestCardForm();
        cardForm.setCardManufacturer(TestCardManufacturerForm);
        List<TestCardVo> cardList = testCardMapper.findList(cardForm);
        if(cardList != null && cardList.size() > 0){
            throw new FrameworkRuntimeException(ResultCode.Fail, "厂商下面已经存在卡片不允许删除!");
        }
        TestCardManufacturerForm.setDelFlg(ResultCode.DEL);
        dao.delete(TestCardManufacturerForm);
    }
}
