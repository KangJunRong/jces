package com.ecp.jces.server.dc.service.terminal.impl;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.form.TestMatrixCardForm;
import com.ecp.jces.form.TestMatrixForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixCardMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixMapper;
import com.ecp.jces.server.dc.service.terminal.TestMatrixService;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.server.util.BeanUtils;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.TestEngineVo;
import com.ecp.jces.vo.TestMatrixCardVo;
import com.ecp.jces.vo.TestMatrixVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TestMatrixServiceImpl implements TestMatrixService {
    private static final Logger LOGGER = LogManager.getLogger(TestMatrixServiceImpl.class);
    @Autowired
    private TestMatrixMapper testMatrixMapper;
    @Autowired
    private TestMatrixCardMapper testMatrixCardMapper;

    @Autowired
    private TestEngineMapper testEngineMapper;

    @Autowired
    private CenterInf centerInf;

    @Autowired
    private RedisDao redisDao;

    //  接口数据插入数据库
    @Override
    public void getTestMatrixInformation(String engineId) {
        if(redisDao.getValue(ConstantCode.UPDATE_ENGINE_INFO + engineId) != null){
            throw new FrameworkRuntimeException(ResultCode.Fail, "接口已调用,请勿重复调用");
        }

        TestEngineForm testEngineForm = new TestEngineForm();
        testEngineForm.setId(engineId);
        TestEngineVo engineVo = testEngineMapper.getById(testEngineForm);
        if (engineVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "测试引擎不存在");
        }
        centerInf.getTestMatrixInformation(engineVo);

        redisDao.setValueTtl(ConstantCode.UPDATE_ENGINE_INFO + engineId, engineId, 30L);

        while (redisDao.getValue(ConstantCode.UPDATE_ENGINE_INFO + engineId) != null){
            try {
                //LOGGER.info("uploadEngineInfo 未上报：" + engineId);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("uploadEngineInfo 已经上报：" + engineId);
    }

    @Override
    public void updateStatus(TestMatrixForm form) {
        testMatrixMapper.update(form);
    }

    @Override
    public List<TestMatrixVo> getListByEngineId(TestMatrixForm form) {
        List<TestMatrixVo> list = testMatrixMapper.list(form);
        for (TestMatrixVo vo : list) {
            List<TestMatrixCardVo> list1 = testMatrixCardMapper.findByMatrix(vo.getMatrixId());
            vo.setCardInfo(list1);
        }
        return list;
    }

    @Override
    public Pagination<TestMatrixVo> page(TestMatrixForm form) {
        Page<Object> page = PageHelper.startPage(form.getPage(), form.getPageCount());
        List<TestMatrixVo> list = testMatrixMapper.list(form);
        for (TestMatrixVo vo : list) {
            List<TestMatrixCardVo> list1 = testMatrixCardMapper.findByMatrix(vo.getMatrixId());
            vo.setCardInfo(list1);
        }
        Pagination<TestMatrixVo> pagination = new Pagination<>(form.getPage(), form.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(page.getTotal());
        return pagination;
    }

    @Override
    public TestMatrixVo getMatrixInfo(TestMatrixForm form) {
        TestEngineForm testEngineForm = new TestEngineForm();
        testEngineForm.setId(form.getEngineId());
        TestEngineVo engineVo = testEngineMapper.getById(testEngineForm);
        if (engineVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "测试引擎不存在");
        }
        TestMatrixVo testMatrixVo = centerInf.getMatrixInfo(engineVo, form.getMatrixId());

        testMatrixCardMapper.deleteByMatrixId(form.getMatrixId());

        TestMatrixForm matrixForm = new TestMatrixForm();
        matrixForm.setMatrixId(testMatrixVo.getMatrixId());
        matrixForm.setMatrixName(testMatrixVo.getMatrixName());
        matrixForm.setCardCount(testMatrixVo.getCardCount());
        matrixForm.setMatrixStatus(testMatrixVo.getMatrixStatus());
        matrixForm.setVersionNo(testMatrixVo.getVersionNo());
        testMatrixMapper.update(matrixForm);
        List<TestMatrixCardForm> list = BeanUtils.copy(testMatrixVo.getCardInfo(), TestMatrixCardForm.class);
        for (TestMatrixCardForm cardForm : list) {
            cardForm.setId(StrUtil.newGuid());
            cardForm.setMatrixId(form.getMatrixId());
            cardForm.setEngineId(form.getEngineId());
        }
        testMatrixCardMapper.add(list);
        return testMatrixVo;
    }
}
