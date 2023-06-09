package com.ecp.jces.server.dc.service.terminal.impl;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestCardForm;
import com.ecp.jces.form.TestCardManufacturerForm;
import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.form.TestEngineReaderForm;
import com.ecp.jces.form.extra.CardInfoForm;
import com.ecp.jces.server.dc.mapper.sys.SysConfigMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestCardManufacturerMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestCardMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineReaderMapper;
import com.ecp.jces.server.dc.service.terminal.TestEngineReaderService;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.server.util.BeanUtils;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TestEngineReaderServiceImpl implements TestEngineReaderService {
    private static final Logger logger = LoggerFactory.getLogger(TestEngineReaderServiceImpl.class);

    @Autowired
    private TestEngineReaderMapper dao;
    @Autowired
    private CenterInf centerInf;
    @Autowired
    private TestEngineMapper testEngineMapper;

    @Autowired
    private TestCardMapper testCardMapper;

    @Autowired
    private TestCardManufacturerMapper testCardManufacturerMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public List<TestEngineReaderVo> findList(TestEngineReaderForm testEngineReaderForm) throws FrameworkRuntimeException {
        return dao.findList(testEngineReaderForm);
    }

    @Override
    public Pagination<TestEngineReaderVo> page(TestEngineReaderForm testEngineReaderForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(testEngineReaderForm.getPage(), testEngineReaderForm.getPageCount());
        List<TestEngineReaderVo> list = dao.findList(testEngineReaderForm);
        Pagination<TestEngineReaderVo> pagination = new Pagination<>(testEngineReaderForm.getPage(), testEngineReaderForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void bindCard(String id, String testCardId) throws FrameworkRuntimeException {
        dao.bindCard(id, testCardId);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void unbindCard(String id) throws FrameworkRuntimeException {
        dao.unbindCard(id);
    }

    @Override
    @Transactional(rollbackFor = FrameworkRuntimeException.class)
    public void syncReaders(TestEngineReaderForm testEngineReaderForm) throws FrameworkRuntimeException {
        TestEngineForm testEngineForm = new TestEngineForm();
        testEngineForm.setId(testEngineReaderForm.getTestEngine().getId());
        TestEngineVo vo = testEngineMapper.getById(testEngineForm);
        if (vo != null) {
            if (TestEngineVo.TESTING_STATUS.equals(vo.getStatus())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "测试引擎测试卡片中,不能获取读卡列表");
            }
            if (TestEngineVo.OFFLINE_STATUS.equals(vo.getStatus())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "测试引擎离线,不能获取读卡列表");
            }
            if (TestEngineVo.STOP_STATUS.equals(vo.getStatus())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "测试引擎没启用,不能获取读卡列表");
            }
            if (StrUtil.isBlank(vo.getIp()) || StrUtil.isBlank(vo.getPort())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "测试引擎没设置IP或端口,不能获取读卡列表");
            }

            //该引擎下全部设为离线先
            dao.setOffStatusByEngineId(vo.getId(), TestEngineReaderVo.OFFLINE_STATUS);

            List<CardInfoForm> list = centerInf.getReaderCards(vo);
            List<SysConfigVo> params = sysConfigMapper.selectAll();
            Map<String, String> paramMap = new HashMap<>();
            if (params.size() != 0) {
                paramMap = params.stream().collect(Collectors.toMap(SysConfigVo::getLabel, SysConfigVo::getValue));
            }
            if (list != null && list.size() > 0) {
                Map<String, TestCardManufacturerVo> cardManufacturerMap = testCardManufacturerMapper.allForCode();

                //下版本优化
                for (CardInfoForm form : list) {
                    Date date = new Date();
                    UserVo user = new UserVo();
                    user.setId(ConstantCode.SYSTEM_ADMIN_ID);
                    //找出有没有该厂商,没有自动加进去数据库
                    TestCardManufacturerVo testCardManufacturerVo;
                    TestCardVo testCardVo = null;
                    if (StrUtil.isNotBlank(form.getShorterName())) {
                        testCardManufacturerVo = cardManufacturerMap.get(form.getShorterName());
                        if (testCardManufacturerVo == null) {
                            //插入数据库
                            TestCardManufacturerForm cardManufacturerForm = new TestCardManufacturerForm();
                            cardManufacturerForm.setName(paramMap.get(form.getShorterName()));
                            if(cardManufacturerForm.getName() == null){
                                cardManufacturerForm.setName(form.getShorterName());
                            }
                            cardManufacturerForm.setCode(form.getShorterName());
                            cardManufacturerForm.setDelFlg(ResultCode.NOT_DEL);
                            cardManufacturerForm.setCreateUser(user);
                            cardManufacturerForm.setUpdateUser(user);
                            cardManufacturerForm.setCreateDate(date);
                            cardManufacturerForm.setUpdateDate(date);
                            cardManufacturerForm.setId(StrUtil.newGuid());
                            testCardManufacturerMapper.insert(cardManufacturerForm);
                            testCardManufacturerVo = BeanUtils.copy(cardManufacturerForm, TestCardManufacturerVo.class);

                            //循环时存在不用再插入
                            cardManufacturerMap.put(cardManufacturerForm.getCode(), testCardManufacturerVo);

                            //该厂商肯定没有该卡类型,自动加进去数据库
                            if (StrUtil.isNotBlank(form.getCardTypeName())) {
                                TestCardForm testCardForm = new TestCardForm();
                                testCardForm.setDelFlg(ResultCode.NOT_DEL);
                                testCardForm.setCreateUser(user);
                                testCardForm.setUpdateUser(user);
                                testCardForm.setCreateDate(date);
                                testCardForm.setUpdateDate(date);
                                testCardForm.setId(StrUtil.newGuid());
                                testCardForm.setModel(form.getCardTypeName());
                                testCardForm.setName(form.getCardTypeName());
                                testCardForm.setCardManufacturer(cardManufacturerForm);
                                testCardMapper.insert(testCardForm);
                                testCardVo = BeanUtils.copy(testCardForm, TestCardVo.class);
                            }
                        }
                        //厂家存在
                        else {
                            TestCardManufacturerForm cardManufacturerForm = new TestCardManufacturerForm();
                            cardManufacturerForm.setId(testCardManufacturerVo.getId());
                            cardManufacturerForm.setName(paramMap.get(form.getShorterName()));
                            if(cardManufacturerForm.getName() == null){
                                cardManufacturerForm.setName(form.getShorterName());
                            }
                            testCardManufacturerMapper.updateName(cardManufacturerForm);

                            if (StrUtil.isNotBlank(form.getCardTypeName())) {
                                testCardVo = testCardMapper.findByManufacturerAndModel(testCardManufacturerVo.getId(), form.getCardTypeName());
                                if (testCardVo == null) {
                                    TestCardForm testCardForm = new TestCardForm();
                                    testCardForm.setDelFlg(ResultCode.NOT_DEL);
                                    testCardForm.setCreateUser(user);
                                    testCardForm.setUpdateUser(user);
                                    testCardForm.setCreateDate(date);
                                    testCardForm.setUpdateDate(date);
                                    testCardForm.setId(StrUtil.newGuid());
                                    testCardForm.setModel(form.getCardTypeName());
                                    testCardForm.setName(form.getCardTypeName());
                                    testCardForm.setCardManufacturer(BeanUtils.copy(testCardManufacturerVo, TestCardManufacturerForm.class));
                                    testCardMapper.insert(testCardForm);
                                    testCardVo = BeanUtils.copy(testCardForm, TestCardVo.class);
                                }
                            }
                        }
                    }

                    TestCardForm bindCard = new TestCardForm();
                    if (testCardVo != null) {
                        bindCard = BeanUtils.copy(testCardVo, TestCardForm.class);
                    }

                    //找出引擎下有没有该读卡器,没有自动加进去数据库,有则修改
                    TestEngineReaderVo engineReaderVo = dao.findByEngineIdAndName(vo.getId(), form.getReaderName());
                    TestEngineReaderForm readerForm = new TestEngineReaderForm();
                    if (engineReaderVo == null) {
                        readerForm.setDelFlg(ResultCode.NOT_DEL);
                        readerForm.setCreateUser(user);
                        readerForm.setUpdateUser(user);
                        readerForm.setCreateDate(date);
                        readerForm.setUpdateDate(date);
                        readerForm.setId(StrUtil.newGuid());
                        readerForm.setTestEngine(testEngineForm);
                        readerForm.setName(form.getReaderName());
                        readerForm.setStatus(form.getReaderStatus());
                        readerForm.setBindCard(bindCard);
                        dao.add(readerForm);
                    } else {
                        readerForm.setUpdateUser(user);
                        readerForm.setUpdateDate(date);
                        readerForm.setId(engineReaderVo.getId());
                        readerForm.setTestEngine(testEngineForm);
                        readerForm.setName(form.getReaderName());
                        readerForm.setStatus(form.getReaderStatus());
                        readerForm.setBindCard(bindCard);
                        dao.updateByEngine(readerForm);
                    }
                }
            }
        } else {
            throw new FrameworkRuntimeException(ResultCode.Fail, "不存在的测试引擎");
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void setReadersOffLine(TestEngineReaderForm testEngineReaderForm) throws FrameworkRuntimeException {
        //该引擎下全部设为离线先
        dao.setOffStatusByEngineId(testEngineReaderForm.getTestEngine().getId(), TestEngineReaderVo.OFFLINE_STATUS);
    }
}
