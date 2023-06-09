package com.ecp.jces.server.dc.service.terminal.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestCardGroupForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.terminal.TestCardGroupMapper;
import com.ecp.jces.server.dc.service.terminal.TestCardGroupRelateService;
import com.ecp.jces.server.dc.service.terminal.TestCardGroupService;
import com.ecp.jces.server.dc.service.terminal.TestCardService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestCardGroupVo;
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
public class TestCardGroupServiceImpl implements TestCardGroupService {

    private static final Logger logger = LoggerFactory.getLogger(TestCardGroupServiceImpl.class);

    @Autowired
    private TestCardGroupMapper dao;
    @Autowired
    private TestCardService testCardService;
    @Autowired
    private TestCardGroupRelateService testCardGroupRelateService;

    @Override
    public List<TestCardGroupVo> findList(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException {
        return dao.findList(testCardGroupForm);
    }

    @Override
    public Pagination<TestCardGroupVo> page(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(testCardGroupForm.getPage(), testCardGroupForm.getPageCount());
        List<TestCardGroupVo> list = dao.findList(testCardGroupForm);
        Pagination<TestCardGroupVo> pagination = new Pagination<>(testCardGroupForm.getPage(), testCardGroupForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void add(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException {
        VerificationUtils.string("name", testCardGroupForm.getName(), false, 64);
        TestCardGroupVo oldCardGroup = dao.getByName(testCardGroupForm);
        if(oldCardGroup != null){
            throw new FrameworkRuntimeException(ResultCode.Fail, "名称已经存在!");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testCardGroupForm.setId(StrUtil.newGuid());
        testCardGroupForm.setCreateUser(vo);
        testCardGroupForm.setCreateDate(date);
        testCardGroupForm.setUpdateUser(vo);
        testCardGroupForm.setUpdateDate(date);
        testCardGroupForm.setDelFlg(ResultCode.NOT_DEL);
        testCardGroupForm.setStatus(TestCardGroupVo.EDIT_STATUS);
        dao.insert(testCardGroupForm);

        //插入卡片组-卡片关系表
        testCardGroupRelateService.addBatch(testCardGroupForm.getId(), testCardGroupForm.getCardIds());
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void update(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException {
        VerificationUtils.string("name", testCardGroupForm.getName(), false, 64);
        TestCardGroupVo oldCardGroup = dao.getByName(testCardGroupForm);
        if(oldCardGroup != null && (!oldCardGroup.getId().equals(testCardGroupForm.getId()))){
            throw new FrameworkRuntimeException(ResultCode.Fail, "名称已经存在!");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testCardGroupForm.setUpdateUser(vo);
        testCardGroupForm.setUpdateDate(date);
        dao.update(testCardGroupForm);

        testCardGroupRelateService.deleteByCardGroupId(testCardGroupForm.getId());

        //插入卡片组-卡片关系表
        testCardGroupRelateService.addBatch(testCardGroupForm.getId(), testCardGroupForm.getCardIds());
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void delete(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException {
        testCardGroupForm.setDelFlg(ResultCode.DEL);
        dao.delete(testCardGroupForm);
        //删除卡片组里面的卡片
        testCardGroupRelateService.deleteByCardGroupId(testCardGroupForm.getId());
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void publish(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException {
        //更新状态为发布状态
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testCardGroupForm.setUpdateUser(vo);
        testCardGroupForm.setUpdateDate(date);
        dao.updateStatus(testCardGroupForm);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void active(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException {
        //更新状态为激活状态
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testCardGroupForm.setUpdateUser(vo);
        testCardGroupForm.setUpdateDate(date);
        dao.updateStatus(testCardGroupForm);

        //其他激活状态的组设置为发布状态
        dao.changeOtherActiveStatusToPublishStatus(testCardGroupForm);
    }

    @Override
    public TestCardGroupVo getById(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException {
        return dao.getById(testCardGroupForm);
    }


}
