package com.ecp.jces.server.dc.service.terminal.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestCardGroupRelateForm;
import com.ecp.jces.server.dc.mapper.terminal.TestCardGroupRelateMapper;
import com.ecp.jces.server.dc.service.terminal.TestCardGroupRelateService;
import com.ecp.jces.vo.TestCardGroupRelateVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCardGroupRelateServiceImpl implements TestCardGroupRelateService {

    @Autowired
    private TestCardGroupRelateMapper dao;
    @Override
    @Transactional(readOnly = false)
    public void add(TestCardGroupRelateForm testCardGroupRelateForm) throws FrameworkRuntimeException {
        dao.insert(testCardGroupRelateForm);
    }

    @Override
    @Transactional(readOnly = false)
    public void addBatch(String cardGroupId, String cardIds) throws FrameworkRuntimeException {
        if(StringUtils.isBlank(cardGroupId) || StringUtils.isBlank(cardIds)){
            throw new FrameworkRuntimeException(ResultCode.Fail, "批量插入卡片到卡片组失败！");
        }
        String[] cardIdArray = cardIds.split(",");
        List<TestCardGroupRelateForm> list = new ArrayList<>();
        for(String cardId : cardIdArray){
            if(StringUtils.isNotBlank(cardId)){
                TestCardGroupRelateForm testCardGroupRelateForm = new TestCardGroupRelateForm();
                testCardGroupRelateForm.setCardGroupId(cardGroupId);
                testCardGroupRelateForm.setCardId(cardId);
                list.add(testCardGroupRelateForm);
            }
        }
        dao.insertBatch(list);

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByCardGroupId(String cardGroupId) throws FrameworkRuntimeException {
        dao.deleteByCardGroupId(cardGroupId);
    }

    @Override
    public List<TestCardGroupRelateVo> findList(TestCardGroupRelateForm testCardGroupRelateForm) throws FrameworkRuntimeException {
        return dao.findList(testCardGroupRelateForm);
    }
}
