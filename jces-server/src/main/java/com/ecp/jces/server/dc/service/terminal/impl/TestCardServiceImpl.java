package com.ecp.jces.server.dc.service.terminal.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestCardForm;
import com.ecp.jces.form.TestCardGroupRelateForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.terminal.TestCardMapper;
import com.ecp.jces.server.dc.service.terminal.TestCardGroupRelateService;
import com.ecp.jces.server.dc.service.terminal.TestCardService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestCardGroupRelateVo;
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
public class TestCardServiceImpl implements TestCardService {
    private static final Logger logger = LoggerFactory.getLogger(TestCardServiceImpl.class);

    @Autowired
    private TestCardMapper dao;
    @Autowired
    private TestCardGroupRelateService testCardGroupRelateService;

    @Override
    public List<TestCardVo> findList(TestCardForm testCardForm) throws FrameworkRuntimeException {
        return dao.findList(testCardForm);
    }

    @Override
    public Pagination<TestCardVo> page(TestCardForm testCardForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(testCardForm.getPage(), testCardForm.getPageCount());
        List<TestCardVo> list = dao.findList(testCardForm);
        Pagination<TestCardVo> pagination = new Pagination<>(testCardForm.getPage(), testCardForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void add(TestCardForm testCardForm) throws FrameworkRuntimeException {

        VerificationUtils.string("model", testCardForm.getModel(), false, 64);
        if(testCardForm.getCardManufacturer() == null || (testCardForm.getCardManufacturer() != null && StringUtils.isBlank(testCardForm.getCardManufacturer().getId()))){
            throw new FrameworkRuntimeException(ResultCode.Fail, "卡片厂商不能为空!");
        }
        //校验卡片型号和卡片厂商
        TestCardVo oldCard = dao.getByModelAndCardManufacturer(testCardForm);
        if(oldCard != null){
            throw new FrameworkRuntimeException(ResultCode.Fail, "该厂商下卡片型号已经存在!");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testCardForm.setId(StrUtil.newGuid());
        testCardForm.setCreateUser(vo);
        testCardForm.setCreateDate(date);
        testCardForm.setUpdateUser(vo);
        testCardForm.setUpdateDate(date);
        testCardForm.setDelFlg(ResultCode.NOT_DEL);
        dao.insert(testCardForm);
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void update(TestCardForm testCardForm) throws FrameworkRuntimeException {
        VerificationUtils.string("model", testCardForm.getModel(), false, 64);
        if(testCardForm.getCardManufacturer() == null || (testCardForm.getCardManufacturer() != null && StringUtils.isBlank(testCardForm.getCardManufacturer().getId()))){
            throw new FrameworkRuntimeException(ResultCode.Fail, "卡片厂商不能为空!");
        }
        //校验卡片型号和卡片厂商
        TestCardVo oldCard = dao.getByModelAndCardManufacturer(testCardForm);
        if(oldCard != null && !oldCard.getId().equals(testCardForm.getId())){
            throw new FrameworkRuntimeException(ResultCode.Fail, "此厂商下卡片型号已经存在!");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testCardForm.setUpdateUser(vo);
        testCardForm.setUpdateDate(date);
        dao.update(testCardForm);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void delete(TestCardForm testCardForm) throws FrameworkRuntimeException {
        TestCardGroupRelateForm testCardGroupRelateForm = new TestCardGroupRelateForm();
        testCardGroupRelateForm.setCardId(testCardForm.getId());
        List<TestCardGroupRelateVo> testCardGroupRelateList = testCardGroupRelateService.findList(testCardGroupRelateForm);
        if(testCardGroupRelateList != null && testCardGroupRelateList.size() > 0){
            throw new FrameworkRuntimeException(ResultCode.Fail, "删除失败！此卡片已经被分配到卡片组中");
        }
        testCardForm.setDelFlg(ResultCode.DEL);
        dao.delete(testCardForm);
    }

    @Override
    public Pagination<TestCardVo> pageByCardGroup(TestCardForm testCardForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(testCardForm.getPage(), testCardForm.getPageCount());
        List<TestCardVo> list = dao.findListByCardGroup(testCardForm);
        Pagination<TestCardVo> pagination = new Pagination<>(testCardForm.getPage(), testCardForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }
}
