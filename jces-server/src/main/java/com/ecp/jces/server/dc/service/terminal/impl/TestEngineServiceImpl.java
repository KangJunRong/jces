package com.ecp.jces.server.dc.service.terminal.impl;

import com.ecp.jces.code.ConfigKey;
import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.*;
import com.ecp.jces.form.extra.ApduDataForm;
import com.ecp.jces.form.extra.ResultForm;
import com.ecp.jces.jctool.detection.model.PerformanceAnalysis;
import com.ecp.jces.jctool.exception.TlvAnalysisException;
import com.ecp.jces.jctool.simulator.InstallItem;
import com.ecp.jces.jctool.util.GPUtil;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.mapper.applet.*;
import com.ecp.jces.server.dc.mapper.sys.SysConfigMapper;
import com.ecp.jces.server.dc.mapper.task.*;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineReaderMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixCardMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixMapper;
import com.ecp.jces.server.dc.service.terminal.TestEngineService;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.server.util.DateUtil;
import com.ecp.jces.server.util.IPWhiteListUtil;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.*;
import com.ecp.jces.vo.extra.JavaCardDataVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestEngineServiceImpl implements TestEngineService {
    private static final Logger logger = LoggerFactory.getLogger(TestEngineServiceImpl.class);
    @Autowired
    private TestMatrixCardMapper testMatrixCardMapper;
    @Autowired
    private TestEngineMapper dao;
    @Autowired
    private TestEngineReaderMapper testEngineReaderMapper;
    @Autowired
    private TestScheduleMapper testScheduleMapper;
    @Autowired
    private TestTaskMapper testTaskMapper;
    @Autowired
    private TestCheckReportMapper testCheckReportMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TestMatrixMapper testMatrixMapper;
    @Autowired
    private TestReportDataMapper testReportDataMapper;
    @Autowired
    private TestReportApduMapper testReportApduMapper;
    @Autowired
    private AppletMapper appletMapper;
    @Autowired
    private AppletVersionMapper appletVersionMapper;
    @Autowired
    private TestReportMapper testReportMapper;
    @Autowired
    private AppletExeLoadFileMapper appletExeLoadFileMapper;
    @Autowired
    private AppletExeModuleMapper appletExeModuleMapper;
    @Autowired
    private TestBusinessScriptMapper testBusinessScriptMapper;
    @Autowired
    private CenterInf centerInf;

    private void sendUploadEngine(String matrixId) {
        TestMatrixVo matrixVo = testMatrixMapper.findById(matrixId);
        if (matrixVo != null) {
            TestEngineVo vo = new TestEngineVo();
            vo.setId(matrixVo.getEngineId());
            dao.updateExMsg(matrixVo.getEngineId(), null);
            centerInf.getTestMatrixInformation(vo);
        }
    }

    @Override
    public List<TestEngineVo> findList(TestEngineForm testEngineForm) throws FrameworkRuntimeException {
        return dao.findList(testEngineForm);
    }

    @Override
    public Pagination<TestEngineVo> page(TestEngineForm testEngineForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(testEngineForm.getPage(), testEngineForm.getPageCount());
        List<TestEngineVo> list = dao.findList(testEngineForm);
        Pagination<TestEngineVo> pagination = new Pagination<>(testEngineForm.getPage(), testEngineForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public TestEngineVo getById(TestEngineForm form) throws FrameworkRuntimeException {
        return dao.getById(form);
    }

    @Override
    public void vmCosResult(String ip, JavaCardDataVo javaCardDataVo) throws FrameworkRuntimeException {
        checkEngineIp(ip);

        TestTaskVo testTaskVo = testTaskMapper.findById(javaCardDataVo.getTestTaskId());
        if (testTaskVo == null) {
            logger.info("不合法的测试任务ID:" + javaCardDataVo.getTestTaskId());
            return;
        }

        TestCheckReportVo testCheckReportVo = testCheckReportMapper.findByAppletIdAndVersionId(testTaskVo.getAppletId(), testTaskVo.getAppletVersionId());
        //没有表示没下发或者没有结果上报,有结果表示已经完成虚拟cos测试
        if (testCheckReportVo != null) {
            handleVmCosResult(javaCardDataVo);
            return;
        }

        List<SysConfigVo> sysConfigVos = sysConfigMapper.list(ConstantCode.SYS_CONF_TEXT_PARAM);
        Map<String, String> sysConfigMap = new HashMap<>();
        if (sysConfigVos != null) {
            sysConfigMap = sysConfigVos.stream().collect(Collectors.toMap(SysConfigVo::getLabel, SysConfigVo::getValue));
        }

        Date date = new Date();
        TestCheckReportForm form = new TestCheckReportForm();
        form.setAppletId(testTaskVo.getAppletId());
        form.setAppletVersionId(testTaskVo.getAppletVersionId());
        form.setId(StrUtil.newGuid());
        form.setCreateUser(ConstantCode.SYSTEM_ADMIN_ID);
        form.setCreateDate(date);
        form.setUpdateUser(ConstantCode.SYSTEM_ADMIN_ID);
        form.setUpdateDate(date);
        form.setDelFlg(ResultCode.NOT_DEL);
        /***  installItem  **/
        if (javaCardDataVo.getInstallItem() == null) {
            javaCardDataVo.setInstallItem(new InstallItem());
        }
        form.setMemoryDtrSize(javaCardDataVo.getInstallItem().getDtrSpace());
        form.setMemoryDtrSizeExpect(sysConfigMap.get(ConfigKey.MemoryDtrSizeExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.MemoryDtrSizeExpect)));
        form.setMemoryRtrSize(javaCardDataVo.getInstallItem().getRtrSpace());
        form.setMemoryRtrSizeExpect(sysConfigMap.get(ConfigKey.MemoryRtrSizeExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.MemoryRtrSizeExpect)));
        form.setMemoryCodeSize(javaCardDataVo.getInstallItem().getNvmSpace());
        form.setMemoryCodeSizeExpect(sysConfigMap.get(ConfigKey.MemoryCodeSizeExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.MemoryCodeSizeExpect)));

        form.setInstallNewAmount(javaCardDataVo.getInstallItem().getNewObjectCount());
        form.setInstallNewAmountExpect(sysConfigMap.get(ConfigKey.InstallNewAmountExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.InstallNewAmountExpect)));
        form.setInstallNewSpace(javaCardDataVo.getInstallItem().getNewObjectSpace());
        form.setInstallNewSpaceExpect(sysConfigMap.get(ConfigKey.InstallNewSpaceExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.InstallNewSpaceExpect)));

        form.setInstallNewArrayAmount(javaCardDataVo.getInstallItem().getNewArrayCount());
        form.setInstallNewArrayAmountExpect(sysConfigMap.get(ConfigKey.InstallNewArrayAmountExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.InstallNewArrayAmountExpect)));
        form.setInstallNewArraySpace(javaCardDataVo.getInstallItem().getNewArraySpace());
        form.setInstallNewArraySpaceExpect(sysConfigMap.get(ConfigKey.InstallNewArraySpaceExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.InstallNewArraySpaceExpect)));
        /***  performanceAnalysis  **/
        if (javaCardDataVo.getPerformanceAnalysis() == null) {
            javaCardDataVo.setPerformanceAnalysis(new PerformanceAnalysis());
        }
        form.setStaticReferenceArrayInitAmount(javaCardDataVo.getPerformanceAnalysis().getReferenceArrayInitAmount());
        form.setStaticReferenceArrayInitAmountExpect(sysConfigMap.get(ConfigKey.StaticReferenceArrayInitAmountExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.StaticReferenceArrayInitAmountExpect)));

        form.setStaticReferenceArrayInitSpace(javaCardDataVo.getPerformanceAnalysis().getReferenceArrayInitSpace());
        form.setStaticReferenceArrayInitSpaceExpect(sysConfigMap.get(ConfigKey.StaticReferenceArrayInitSpaceExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.StaticReferenceArrayInitSpaceExpect)));

        form.setStaticReferenceNullAmount(javaCardDataVo.getPerformanceAnalysis().getReferenceNullAmount());
        form.setStaticReferenceNullAmountExpect(sysConfigMap.get(ConfigKey.StaticReferenceNullAmountExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.StaticReferenceNullAmountExpect)));

        form.setStaticReferenceNullSpace(javaCardDataVo.getPerformanceAnalysis().getReferenceNullSpace());
        form.setStaticReferenceNullSpaceExpect(sysConfigMap.get(ConfigKey.StaticReferenceNullSpaceExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.StaticReferenceNullSpaceExpect)));

        form.setStaticPrimitiveDefaultAmount(javaCardDataVo.getPerformanceAnalysis().getPrimitiveDefaultAmount());
        form.setStaticPrimitiveDefaultAmountExpect(sysConfigMap.get(ConfigKey.StaticPrimitiveDefaultAmountExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.StaticPrimitiveDefaultAmountExpect)));

        form.setStaticPrimitiveDefaultSpace(javaCardDataVo.getPerformanceAnalysis().getPrimitiveDefaultSpace());
        form.setStaticPrimitiveDefaultSpaceExpect(sysConfigMap.get(ConfigKey.StaticPrimitiveDefaultSpaceExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.StaticPrimitiveDefaultSpaceExpect)));

        form.setStaticPrimitiveNonDefaultAmount(javaCardDataVo.getPerformanceAnalysis().getPrimitiveNonDefaultAmount());
        form.setStaticPrimitiveNonDefaultAmountExpect(sysConfigMap.get(ConfigKey.StaticPrimitiveNonDefaultAmountExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.StaticPrimitiveNonDefaultAmountExpect)));

        form.setStaticPrimitiveNonDefaultSpace(javaCardDataVo.getPerformanceAnalysis().getPrimitiveNonDefaultSpace());
        form.setStaticPrimitiveNonDefaultSpaceExpect(sysConfigMap.get(ConfigKey.StaticPrimitiveNonDefaultSpaceExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.StaticPrimitiveNonDefaultSpaceExpect)));

        //期望的参数
        form.setDownloadInstallTimeExpect(sysConfigMap.get(ConfigKey.downloadInstallTimeExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.downloadInstallTimeExpect)));
        form.setDownloadMaxTimeExpect(sysConfigMap.get(ConfigKey.downloadMaxTimeExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.downloadMaxTimeExpect)));
        form.setLoadC7MaxExpect(sysConfigMap.get(ConfigKey.loadC7MaxExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.loadC7MaxExpect)));
        form.setLoadC8MaxExpect(sysConfigMap.get(ConfigKey.loadC8MaxExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.loadC8MaxExpect)));
        form.setInstallC7MaxExpect(sysConfigMap.get(ConfigKey.installC7MaxExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.installC7MaxExpect)));
        form.setInstallC8MaxExpect(sysConfigMap.get(ConfigKey.installC8MaxExpect) == null ?
                0 : Integer.parseInt(sysConfigMap.get(ConfigKey.installC8MaxExpect)));

        if (javaCardDataVo.getPackageInfoList() != null && javaCardDataVo.getPackageInfoList().size() > 0) {
            form.setNonstandardApi(JSONUtils.toJSONString(javaCardDataVo.getPackageInfoList()));
        } else {
            form.setNonstandardApi("[]");
        }

        //敏感API 移到安全审核
        /*if (javaCardDataVo.getSensitiveApiSet() != null) {
            if (javaCardDataVo.getInstallItem().getRtrSpace() > 0) {
                javaCardDataVo.getSensitiveApiSet().add(ConstantCode.SENSITIVE_API_RTR);
            }
            form.setSensitiveApi(JSONUtils.toJSONString(javaCardDataVo.getSensitiveApiSet()));
        } else if (javaCardDataVo.getInstallItem().getRtrSpace() > 0) {
            Set<String> setStr = new HashSet<>();
            setStr.add(ConstantCode.SENSITIVE_API_RTR);
            form.setSensitiveApi(JSONUtils.toJSONString(setStr));
        }*/


        //应用调用禁用注册toolkit 事件
        if (javaCardDataVo.getInstallItem().getEventList() != null) {
            form.setEventList(JSONUtils.toJSONString(javaCardDataVo.getInstallItem().getEventList()));
        }
        testCheckReportMapper.add(form);

        handleVmCosResult(javaCardDataVo);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void uploadResult(String ip, ResultForm form) throws FrameworkRuntimeException {
        checkEngineIp(ip);

        String result = "";
        if (ConstantCode.EXECUTION_TASK_SUCCESS.equals(form.getResult())) {
            result = ConstantCode.APPLET_STATUS_TEST_SUCCESS;
        }
        if (ConstantCode.EXECUTION_TASK_FAIL.equals(form.getResult())) {
            result = ConstantCode.APPLET_STATUS_TEST_FAIL;
        }

        TestTaskVo testTaskVo = testTaskMapper.findById(form.getTestTaskId());
        if (testTaskVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "找不到该任务ID");
        }

        //判断是否生成了日志,没有生成日志则把日志置空
        if (StrUtil.isNotBlank(form.getCommonLogPath())) {
            String commonLogPath = AesUtil2.decryptAES2(form.getCommonLogPath());
            if (StrUtil.isNotBlank(commonLogPath) && commonLogPath.endsWith(ConstantCode.LOG_SUFFIX_ZIP)) {
                form.setCommonLogPath(commonLogPath);
            } else {
                form.setCommonLogPath(null);
            }
        }

        //插入test_schedule表
        TestScheduleForm testScheduleForm = new TestScheduleForm();
        testScheduleForm.setId(StrUtil.newGuid());
        testScheduleForm.setTestTaskId(testTaskVo.getId());
        if (testTaskVo.getParamTest() != null && testTaskVo.getParamTest()) {
            testScheduleForm.setTestContent(ConstantCode.TEST_CONTENT_PARAM);
        } else {
            testScheduleForm.setTestContent(ConstantCode.TEST_CONTENT_GENERAL);
        }

        testScheduleForm.setTestCardId(form.getShorterName() + "-" + form.getCardTypeName());
        testScheduleForm.setTestEngineReaderId(form.getReaderName());
        testScheduleForm.setStatus(result);
        testScheduleForm.setTestStart(DateUtil.StringToDateTime(form.getTestStart()));
        testScheduleForm.setTestEnd(DateUtil.StringToDateTime(form.getTestEnd()));
        testScheduleForm.setScheduleDate(testScheduleForm.getTestStart());
        testScheduleForm.setDelFlg(ResultCode.NOT_DEL);
        testScheduleMapper.add(testScheduleForm);

        //更新测试报告
        TestReportForm testReportForm = new TestReportForm();
        testReportForm.setId(StrUtil.newGuid());
        testReportForm.setTestScheduleId(testScheduleForm.getId());
        testReportForm.setAppletId(testTaskVo.getAppletId());
        testReportForm.setAppletVersionId(testTaskVo.getAppletVersionId());
        testReportForm.setResult(ConstantCode.APPLET_STATUS_TEST_SUCCESS.equals(result) ?
                ConstantCode.TEST_REPORT_RESULT_SUCCESS : ConstantCode.TEST_REPORT_RESULT_FAIL);
        testReportForm.setLogPath(form.getCommonLogPath());
        testReportForm.setDelFlg(ResultCode.NOT_DEL);
        testReportForm.setErrorInfo(form.getErrorInfo());
        handleResult(form, testReportForm);
        testReportMapper.add(testReportForm);

        //更新进度
        testTaskMapper.uploadProgress(testTaskVo.getId(), form.getRate());

        //更新结果（只要有一张卡失败，这个测试就是失败的）
        if (ConstantCode.APPLET_STATUS_TEST_FAIL.equals(result)) {
            TestTaskForm testTaskForm = new TestTaskForm();
            testTaskForm.setId(testTaskVo.getId());
            testTaskForm.setStatus(result);
            testTaskMapper.edit(testTaskForm);
        }

        //如果测试结束,进度达到100%
        if (100 == form.getRate()) {
            TestTaskForm testTaskForm = new TestTaskForm();
            testTaskForm.setId(testTaskVo.getId());
            //status == null 说明前面的测试都是成功的
            if (ConstantCode.APPLET_STATUS_TESTING.equals(testTaskVo.getStatus())) {
                testTaskForm.setStatus(result);
            }
            testTaskForm.setTestEnd(new Date());
            testTaskMapper.edit(testTaskForm);

            //矩阵变为空闲状态
            TestMatrixForm matrixForm = new TestMatrixForm();
            matrixForm.setMatrixId(testTaskVo.getMatrixId());
            matrixForm.setMatrixStatus(TestMatrixVo.FREE_STATUS);
            testMatrixMapper.update(matrixForm);

            if (ConstantCode.APPLET_STATUS_TEST_FAIL.equals(result)) {
                AppletForm appletForm = new AppletForm();
                appletForm.setId(testTaskVo.getAppletId());
                appletForm.setStatus(result);
                appletMapper.edit(appletForm);

                AppletVersionForm appletVersionForm = new AppletVersionForm();
                appletVersionForm.setId(testTaskVo.getAppletVersionId());
                appletVersionForm.setStatus(result);
                appletVersionMapper.edit(appletVersionForm);

                //测试完成后更新测试引擎信息
                sendUploadEngine(testTaskVo.getMatrixId());

            } else {
                //这次成功，前几次有失败
                if (ConstantCode.APPLET_STATUS_TEST_FAIL.equals(testTaskVo.getStatus())) {
                    AppletForm appletForm = new AppletForm();
                    appletForm.setId(testTaskVo.getAppletId());
                    appletForm.setStatus(ConstantCode.APPLET_STATUS_TEST_FAIL);
                    appletMapper.edit(appletForm);

                    AppletVersionForm appletVersionForm = new AppletVersionForm();
                    appletVersionForm.setId(testTaskVo.getAppletVersionId());
                    appletVersionForm.setStatus(ConstantCode.APPLET_STATUS_TEST_FAIL);
                    appletVersionMapper.edit(appletVersionForm);

                    //测试完成后更新测试引擎信息
                    sendUploadEngine(testTaskVo.getMatrixId());
                    return;
                }

                //判断该测试是通用测试还是参数测试,若是通用测试，成功后要再一轮参数测试
                if (testTaskVo.getParamTest() == null || !testTaskVo.getParamTest()) {
                    List<AppletExeLoadFileVo> loadFiles = appletMapper.getLoadFiles(testTaskVo.getAppletId(),
                            testTaskVo.getAppletVersionId());
                    //更新新的加载参数和安装参数
                    updateParam(testTaskVo.getId(), loadFiles);

                    //把测试任务改为等待测试 paramTest 改为true
                    testTaskMapper.updateParamTest(testTaskVo.getId());
                } else {
                    AppletForm appletForm = new AppletForm();
                    appletForm.setId(testTaskVo.getAppletId());
                    appletForm.setStatus(ConstantCode.APPLET_STATUS_TEST_SUCCESS);
                    appletMapper.edit(appletForm);

                    AppletVersionForm appletVersionForm = new AppletVersionForm();
                    appletVersionForm.setId(testTaskVo.getAppletVersionId());
                    appletVersionForm.setStatus(ConstantCode.APPLET_STATUS_TEST_SUCCESS);
                    appletVersionMapper.edit(appletVersionForm);

                    //测试完成后更新测试引擎信息
                    sendUploadEngine(testTaskVo.getMatrixId());
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void uploadBusinessResult(String ip, ResultForm form) throws FrameworkRuntimeException {
        checkEngineIp(ip);
        TestTaskVo testTaskVo = testTaskMapper.findById(form.getTestTaskId());
        if (testTaskVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "找不到该任务ID");
        }
        String result = "";
        if (ConstantCode.EXECUTION_TASK_SUCCESS.equals(form.getResult())) {
            result = ConstantCode.APPLET_STATUS_TEST_SUCCESS;
        }
        if (ConstantCode.EXECUTION_TASK_FAIL.equals(form.getResult())) {
            result = ConstantCode.APPLET_STATUS_TEST_FAIL;
        }
        //判断是否生成了日志,没有生成日志则把日志置空
        if (StrUtil.isNotBlank(form.getCustomizeLogPath())) {
            String customizeLogPath = AesUtil2.decryptAES2(form.getCustomizeLogPath());
            if (StrUtil.isNotBlank(customizeLogPath) && customizeLogPath.endsWith(ConstantCode.LOG_SUFFIX_ZIP)) {
                form.setCustomizeLogPath(customizeLogPath);
            } else {
                form.setCustomizeLogPath(null);
            }
        }

        TestScheduleForm testScheduleForm = new TestScheduleForm();
        testScheduleForm.setId(StrUtil.newGuid());
        testScheduleForm.setTestTaskId(testTaskVo.getId());
        testScheduleForm.setTestContent(ConstantCode.TEST_CONTENT_BUSINESS);
        testScheduleForm.setTestCardId(form.getShorterName() + "-" + form.getCardTypeName());
        testScheduleForm.setTestEngineReaderId(form.getReaderName());
        testScheduleForm.setStatus(result);
        testScheduleForm.setTestStart(DateUtil.StringToDateTime(form.getTestStart()));
        testScheduleForm.setTestEnd(DateUtil.StringToDateTime(form.getTestEnd()));
        testScheduleForm.setScheduleDate(testScheduleForm.getTestStart());
        testScheduleForm.setDelFlg(ResultCode.NOT_DEL);
        testScheduleMapper.add(testScheduleForm);

        testScheduleMapper.addLog(StrUtil.newGuid(),
                testScheduleForm.getId(), form.getCustomizeLogPath(), form.getErrorInfo());

        //更新进度
        testTaskMapper.uploadProgress(testTaskVo.getId(), form.getRate());
        //更新结果（只要有一张卡失败，这个测试就是失败的）
        if (ConstantCode.APPLET_STATUS_TEST_FAIL.equals(result)) {
            TestTaskForm testTaskForm = new TestTaskForm();
            testTaskForm.setId(testTaskVo.getId());
            testTaskForm.setStatus(result);
            testTaskMapper.edit(testTaskForm);
        }
        if (100 == form.getRate()) {
            TestTaskForm testTaskForm = new TestTaskForm();
            testTaskForm.setId(testTaskVo.getId());
            //status == null 说明前面的测试都是成功的
            if (ConstantCode.APPLET_STATUS_TESTING.equals(testTaskVo.getStatus())
                    || ConstantCode.APPLET_STATUS_WAITING_TEST.equals(testTaskVo.getStatus())) {
                testTaskForm.setStatus(result);
            }
            testTaskForm.setTestEnd(new Date());
            testTaskMapper.edit(testTaskForm);

            //矩阵变为空闲状态
            TestMatrixForm matrixForm = new TestMatrixForm();
            matrixForm.setMatrixId(testTaskVo.getMatrixId());
            matrixForm.setMatrixStatus(TestMatrixVo.FREE_STATUS);
            testMatrixMapper.update(matrixForm);

            if (ConstantCode.APPLET_STATUS_TEST_FAIL.equals(result)) {
                TestBusinessScriptForm testBusinessScriptForm = new TestBusinessScriptForm();
                testBusinessScriptForm.setId(testTaskVo.getTestBusinessScriptId());
                testBusinessScriptForm.setTestEnd(testTaskForm.getTestEnd());
                testBusinessScriptForm.setStatus(ConstantCode.SCRIPT_STATUS_TEST_FAIL);
                testBusinessScriptMapper.edit(testBusinessScriptForm);
            } else {
                //这次成功，前几次有失败
                TestBusinessScriptForm testBusinessScriptForm = new TestBusinessScriptForm();
                testBusinessScriptForm.setId(testTaskVo.getTestBusinessScriptId());
                testBusinessScriptForm.setTestEnd(testTaskForm.getTestEnd());
                if (ConstantCode.APPLET_STATUS_TEST_FAIL.equals(testTaskVo.getStatus())) {
                    testBusinessScriptForm.setStatus(ConstantCode.SCRIPT_STATUS_TEST_FAIL);

                } else {
                    testBusinessScriptForm.setStatus(ConstantCode.SCRIPT_STATUS_TEST_SUCCESS);
                }
                testBusinessScriptMapper.edit(testBusinessScriptForm);
            }
            //测试完成后更新测试引擎信息
            sendUploadEngine(testTaskVo.getMatrixId());
        }
    }

    @Override
    public void register(TestEngineForm form) throws FrameworkRuntimeException {
        checkEngineIp(form.getIp());
        TestEngineVo testEngineVo = dao.getById(form);
        form.setStatus(TestEngineVo.OFFLINE_STATUS);
        form.setDelFlg(ResultCode.NOT_DEL);
        if (testEngineVo != null) {
            form.setCommDate(new Date());
            dao.update(form);
        } else {
            form.setCreateDate(new Date());
            dao.insert(form);
        }
    }

    @Override
    public void uploadEngineInfo(TestEngineForm form) throws FrameworkRuntimeException {
        checkEngineIp(form.getIp());

        TestEngineVo vo = dao.getById(form);
        if (vo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "不存在的测试引擎Id");
        }

        if (form.getList() == null || form.getList().size() == 0) {
            testMatrixMapper.matrixInfoClean(vo.getId());
            redisDao.delValue(ConstantCode.UPDATE_ENGINE_INFO + vo.getId());
            throw new FrameworkRuntimeException(ResultCode.Fail, "请先配置矩阵信息");
        }

        //先清除旧数据
        testMatrixMapper.matrixInfoClean(vo.getId());

        Date date = new Date();
        List<TestMatrixCardForm> cardList = new ArrayList<>();
        for (TestMatrixForm testMatrixForm : form.getList()) {
            testMatrixForm.setEngineId(vo.getId());
            testMatrixForm.setDelFlg(ResultCode.NOT_DEL);
            testMatrixForm.setCreateUser("引擎自动上报");
            testMatrixForm.setCreateDate(date);
            testMatrixForm.setUpdateDate(date);
            testMatrixForm.setUpdateUser("引擎自动上报");
            if (testMatrixForm.getCardInfo() != null && testMatrixForm.getCardInfo().size() > 0) {
                for (TestMatrixCardForm cardForm : testMatrixForm.getCardInfo()) {
                    cardForm.setMatrixId(testMatrixForm.getMatrixId());
                    cardForm.setId(StrUtil.newGuid());
                    cardForm.setEngineId(vo.getId());
                    cardList.add(cardForm);
                }
            }
        }
        form.setCommDate(date);
        dao.update(form);
        testMatrixMapper.add(form.getList());
        if (cardList.size() > 0) {
            testMatrixCardMapper.add(cardList);
        }
        redisDao.delValue(ConstantCode.UPDATE_ENGINE_INFO + vo.getId());
    }

    @Override
    public void callbackTesting(String ip, TestTaskForm form) throws FrameworkRuntimeException {
        checkEngineIp(ip);
        TestTaskVo vo = testTaskMapper.findById(form.getId());
        if (vo == null) {
            logger.info("不存在的taskId, TestTaskId : " + form.getId());
            return;
        }

        logger.info(" 下发测试失败, TestTaskId : " + form.getId());
        //修改测试任务表
        TestTaskForm testTaskForm = new TestTaskForm();
        testTaskForm.setId(form.getId());
        testTaskForm.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
        testTaskForm.setDelFlg(ResultCode.NOT_DEL);
        testTaskMapper.edit(testTaskForm);


        //预测试不改变应用测试状态
        if (!ConstantCode.TEST_CONTENT_BUSINESS.equals(vo.getType())) {
            AppletForm appletForm = new AppletForm();
            appletForm.setId(vo.getAppletId());
            appletForm.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
            appletMapper.edit(appletForm);
        }

        //更新测试引擎异常
        if (StrUtil.isNotBlank(form.getExMsg()) && StrUtil.isNotBlank(form.getEngineId())) {
            dao.updateExMsg(form.getEngineId(), form.getExMsg());
        }
    }

    @Override
    public void callbackStop(String ip, TestTaskForm tForm) throws FrameworkRuntimeException {
        checkEngineIp(ip);

        logger.info(JSONUtils.toJSONString(tForm));
        TestTaskVo vo = testTaskMapper.findById(tForm.getId());
        if (vo == null) {
            return;
        }

        if (StrUtil.isNotBlank(vo.getMatrixId())) {
            TestMatrixForm matrixForm = new TestMatrixForm();
            matrixForm.setMatrixId(vo.getMatrixId());
            matrixForm.setMatrixStatus(TestMatrixVo.FREE_STATUS);
            testMatrixMapper.update(matrixForm);
        }


        TestTaskForm testTaskForm = new TestTaskForm();
        testTaskForm.setId(vo.getId());
        testTaskForm.setStatus(ConstantCode.APPLET_STATUS_TEST_FAIL);
        testTaskForm.setTestEnd(new Date());
        testTaskMapper.edit(testTaskForm);

        AppletForm form = new AppletForm();
        form.setId(vo.getAppletId());
        form.setStatus(ConstantCode.APPLET_STATUS_TEST_FAIL);
        appletMapper.edit(form);

        AppletVersionForm appletVersionForm = new AppletVersionForm();
        appletVersionForm.setId(vo.getAppletVersionId());
        appletVersionForm.setStatus(ConstantCode.APPLET_STATUS_TEST_FAIL);
        appletVersionMapper.edit(appletVersionForm);

        //更新测试引擎异常
        if (StrUtil.isNotBlank(tForm.getExMsg()) && StrUtil.isNotBlank(tForm.getEngineId())) {
            dao.updateExMsg(tForm.getEngineId(), tForm.getExMsg());
        }
    }

    private void checkEngineIp(String ip) {
        String whiteList = redisDao.getValue(ConstantCode.WHITELIST);
        if (StrUtil.isBlank(whiteList)) {
            return;
        }

        if (!IPWhiteListUtil.isPermited(ip, whiteList)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "非法IP上报!");
        }
    }

    private void handleVmCosResult(JavaCardDataVo javaCardDataVo) {
        //解锁标识
        redisDao.delValue(ConstantCode.VM_COS_TEST_FLAG + javaCardDataVo.getTestTaskId());
        //矩阵变为空闲
        TestMatrixForm testMatrixForm = new TestMatrixForm();
        testMatrixForm.setMatrixId(javaCardDataVo.getMatrixId());
        testMatrixForm.setMatrixStatus(javaCardDataVo.getMatrixStatus());
        testMatrixMapper.update(testMatrixForm);
    }

    private void handleResult(ResultForm form, TestReportForm testReportForm) {
        Date date = new Date();
        if (StrUtil.isNotBlank(form.getLoadData())) {
            String[] loadDataArr = form.getLoadData().split(",");
            List<TestReportDataForm> loadDataList = new ArrayList<>();
            for (String load : loadDataArr) {
                TestReportDataForm testReportDataForm = new TestReportDataForm();
                String[] loadArr = load.split(":");
                if (loadArr.length == 3) {
                    testReportDataForm.setTaskId(form.getTestTaskId());
                    testReportDataForm.setScheduleId(testReportForm.getTestScheduleId());
                    testReportDataForm.setCapName(loadArr[0]);
                    testReportDataForm.setC6("NaN".equals(loadArr[2]) ? 0 : Integer.parseInt(loadArr[2]));
                    testReportDataForm.setC7("NaN".equals(loadArr[1]) ? 0 : Integer.parseInt(loadArr[1]));
                    testReportDataForm.setC8("NaN".equals(loadArr[2]) ? 0 : Integer.parseInt(loadArr[2]));
                    testReportDataForm.setCreateDate(date);
                    loadDataList.add(testReportDataForm);
                }
            }
            testReportDataMapper.addLoadBatch(loadDataList);

            testReportForm.setLoadC8(loadDataList.stream().mapToInt(TestReportDataForm::getC8).sum());
            testReportForm.setLoadC7(loadDataList.stream().mapToInt(TestReportDataForm::getC7).sum());
            testReportForm.setLoadC6(testReportForm.getLoadC8());
        }

        if (StrUtil.isNotBlank(form.getInstallData())) {
            String[] installDataArr = form.getInstallData().split(",");
            List<TestReportDataForm> installDataList = new ArrayList<>();
            for (String install : installDataArr) {
                TestReportDataForm testReportDataForm = new TestReportDataForm();
                String[] installArr = install.split(":");
                if (installArr.length == 3) {
                    testReportDataForm.setTaskId(form.getTestTaskId());
                    testReportDataForm.setScheduleId(testReportForm.getTestScheduleId());
                    testReportDataForm.setCapName(installArr[0]);

                    testReportDataForm.setC7("NaN".equals(installArr[1]) ? 0 : Integer.parseInt(installArr[1]));

                    if ("NaN".equals(installArr[2])) {
                        testReportDataForm.setC6(0);
                        testReportDataForm.setC8(0);
                    } else {
                        testReportDataForm.setC6(Integer.parseInt(installArr[2]));
                        testReportDataForm.setC8(Integer.parseInt(installArr[2]));
                    }

                    testReportDataForm.setCreateDate(date);
                    installDataList.add(testReportDataForm);
                }
            }
            testReportDataMapper.addInstallBatch(installDataList);

            testReportForm.setC8(installDataList.stream().mapToInt(TestReportDataForm::getC8).sum());
            testReportForm.setC7(installDataList.stream().mapToInt(TestReportDataForm::getC7).sum());
            testReportForm.setC6(testReportForm.getC8());
        }

        if (StrUtil.isNotBlank(form.getDownload())) {
            String[] downLoadArr = form.getDownload().split(",");
            List<ApduDataForm> downLoadList = new ArrayList<>();
            apduListHandle(downLoadArr, downLoadList);

            if (downLoadList.size() > 0) {
                TestReportApduForm testReportApduForm = new TestReportApduForm();
                testReportApduForm.setTestScheduleId(testReportForm.getTestScheduleId());
                testReportApduForm.setType(ConstantCode.EXECUTION_PARAM_DOWNLOAD);
                testReportApduForm.setList(downLoadList);
                testReportApduMapper.add(testReportApduForm);

                Optional<ApduDataForm> max = downLoadList.stream().max(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setDownloadMaxTime(new BigDecimal(max.get().getTime()).setScale(3, RoundingMode.HALF_UP));
                testReportForm.setDownloadAllTime(new BigDecimal(downLoadList.stream().mapToDouble(ApduDataForm::getTime).sum()).setScale(3, RoundingMode.HALF_UP));

                Optional<ApduDataForm> min = downLoadList.stream().min(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setDownloadMinTime(new BigDecimal(min.get().getTime()).setScale(3, RoundingMode.HALF_UP));
            }
        }

        if (StrUtil.isNotBlank(form.getInstall())) {
            String[] installArr = form.getInstall().split(",");
            List<ApduDataForm> installList = new ArrayList<>();
            apduListHandle(installArr, installList);

            if (installList.size() > 0) {
                TestReportApduForm testReportApduForm = new TestReportApduForm();
                testReportApduForm.setTestScheduleId(testReportForm.getTestScheduleId());
                testReportApduForm.setType(ConstantCode.EXECUTION_PARAM_INSTALL);
                testReportApduForm.setList(installList);
                testReportApduMapper.add(testReportApduForm);

                Optional<ApduDataForm> max = installList.stream().max(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setInstallMaxTime(new BigDecimal(max.get().getTime()).setScale(3, RoundingMode.HALF_UP));
                testReportForm.setInstallAllTime(new BigDecimal(installList.stream().mapToDouble(ApduDataForm::getTime).sum()).setScale(3, RoundingMode.HALF_UP));

                Optional<ApduDataForm> min = installList.stream().min(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setInstallMinTime(new BigDecimal(min.get().getTime()).setScale(3, RoundingMode.HALF_UP));

            }
        }

        if (StrUtil.isNotBlank(form.getUnload())) {
            String[] unloadArr = form.getUnload().split(",");
            List<ApduDataForm> unloadList = new ArrayList<>();
            apduListHandle(unloadArr, unloadList);

            if (unloadList.size() > 0) {
                TestReportApduForm testReportApduForm = new TestReportApduForm();
                testReportApduForm.setTestScheduleId(testReportForm.getTestScheduleId());
                testReportApduForm.setType(ConstantCode.EXECUTION_PARAM_UNLOAD);
                testReportApduForm.setList(unloadList);
                testReportApduMapper.add(testReportApduForm);

                Optional<ApduDataForm> max = unloadList.stream().max(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setUnloadMaxTime(new BigDecimal(max.get().getTime()).setScale(3, RoundingMode.HALF_UP));
                testReportForm.setUnloadAllTime(new BigDecimal(unloadList.stream().mapToDouble(ApduDataForm::getTime).sum()).setScale(3, RoundingMode.HALF_UP));

                Optional<ApduDataForm> min = unloadList.stream().min(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setUnloadMinTime(new BigDecimal(min.get().getTime()).setScale(3, RoundingMode.HALF_UP));
            }
        }
    }

    private void apduListHandle(String[] arr, List<ApduDataForm> list) {
        if (arr.length == 0) {
            return;
        }

        for (String data : arr) {
            ApduDataForm apduTimeForm = new ApduDataForm();
            String[] apduArr = data.split(":");
            if (apduArr.length == 2) {
                apduTimeForm.setApdu(apduArr[0]);
                apduTimeForm.setTime(Double.parseDouble(apduArr[1]));
                list.add(apduTimeForm);
            }
        }
    }

    /**
     * 更新应用包的加载参数和应用的安装参数
     **/
    private void updateParam(String taskId, List<AppletExeLoadFileVo> loadFiles) {
        if (StrUtil.isBlank(taskId) || loadFiles == null || loadFiles.size() == 0) {
            return;
        }

        Map<String, String> dataMap = new HashMap<>();

        List<TestReportDataVo> loadList = testReportDataMapper.maxLoadList(taskId);
        if (loadList.size() > 0) {
            for (TestReportDataVo dataVo : loadList) {
                dataMap.put(dataVo.getCapName(), GPUtil.genInstallForLoadParam(dataVo.getC6(), dataVo.getC7(), dataVo.getC8()));
            }
        }
        List<TestReportDataVo> installList = testReportDataMapper.maxInstallList(taskId);
        if (installList.size() > 0) {
            for (TestReportDataVo dataVo : installList) {
                try {
                    String installParam = dataVo.getInstallParam();
                    if (StrUtil.isNotBlank(installParam) && ConstantCode.C900.equalsIgnoreCase(installParam)) {
                        installParam = "";
                    }
                    dataMap.put(dataVo.getCapName(), GPUtil.genInstallForInstallParam(dataVo.getC7(), dataVo.getC8(), installParam));
                } catch (TlvAnalysisException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        for (AppletExeLoadFileVo loadFileVo : loadFiles) {
            String loadFileLoadParam = dataMap.get(loadFileVo.getFileName());
            if (StrUtil.isNotBlank(loadFileLoadParam)) {
                AppletExeLoadFileForm form = new AppletExeLoadFileForm();
                form.setId(loadFileVo.getId());
                form.setLoadParam(loadFileLoadParam);
                appletExeLoadFileMapper.edit(form);
            }
            if (loadFileVo.getModuleVoList() != null && loadFileVo.getModuleVoList().size() > 0) {
                for (AppletExeModuleVo exeModuleVo : loadFileVo.getModuleVoList()) {
                    if (exeModuleVo.getInstanceVoList() != null && exeModuleVo.getInstanceVoList().size() > 0) {
                        for (AppletInstanceVo instanceVo : exeModuleVo.getInstanceVoList()) {
                            String installParam = dataMap.get(instanceVo.getInstanceAid());
                            if (StrUtil.isNotBlank(installParam)) {
                                appletExeModuleMapper.updateInstanceInstallParam(instanceVo.getInstanceId(), installParam);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void add(TestEngineForm testEngineForm) throws FrameworkRuntimeException {
        //校验ip
        if (StringUtils.isBlank(testEngineForm.getIp())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "IP必填!");
        }
        TestEngineVo oldEngine = dao.getByIp(testEngineForm);
        if (oldEngine != null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "IP已经存在!");
        }
        VerificationUtils.string("name", testEngineForm.getName(), false, 64);
        TestEngineVo oldEngine1 = dao.getByName(testEngineForm);
        if (oldEngine1 != null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "名称已经存在!");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testEngineForm.setId(StrUtil.newGuid());
        testEngineForm.setStatus(TestEngineVo.STOP_STATUS);
        testEngineForm.setCreateUser(vo);
        testEngineForm.setCreateDate(date);
        testEngineForm.setUpdateUser(vo);
        testEngineForm.setUpdateDate(date);
        testEngineForm.setDelFlg(ResultCode.NOT_DEL);
        dao.insert(testEngineForm);
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void update(TestEngineForm testEngineForm) throws FrameworkRuntimeException {
        //校验IP
        if (StringUtils.isBlank(testEngineForm.getIp())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "IP必填!");
        }
        TestEngineVo oldEngine = dao.getByIp(testEngineForm);
        if (oldEngine != null && !oldEngine.getId().equals(testEngineForm.getId())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "IP已经存在!");
        }
        VerificationUtils.string("name", testEngineForm.getName(), false, 64);
        TestEngineVo oldEngine1 = dao.getByName(testEngineForm);
        if (oldEngine1 != null && (!oldEngine1.getId().equals(testEngineForm.getId()))) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "名称已经存在!");
        }
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testEngineForm.setUpdateUser(vo);
        testEngineForm.setUpdateDate(date);
        dao.update(testEngineForm);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void delete(TestEngineForm testEngineForm) throws FrameworkRuntimeException {
        //如果调度表中引用此测试引擎，不容许删除
        TestScheduleForm testScheduleForm = new TestScheduleForm();
        testScheduleForm.setTestEngineId(testEngineForm.getId());
        testScheduleForm.setDelFlg(ResultCode.NOT_DEL);
        List<TestScheduleVo> testScheduleVoList = testScheduleMapper.list(testScheduleForm);
        if (testScheduleVoList != null && testScheduleVoList.size() > 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该测试机已经被调度过，不允许被删除!");
        }
        //删除测试引擎
        testEngineForm.setDelFlg(ResultCode.DEL);
        dao.delete(testEngineForm);
        //删除测试引擎读卡器
        TestEngineReaderForm engineReaderForm = new TestEngineReaderForm();
        engineReaderForm.setTestEngine(testEngineForm);
        engineReaderForm.setDelFlg(ResultCode.DEL);
        testEngineReaderMapper.delete(engineReaderForm);
    }

    @Override
    public TestEngineVo detail(TestEngineForm testEngineForm) throws FrameworkRuntimeException {
        TestEngineVo testEngineVo = dao.getById(testEngineForm);
        TestEngineReaderForm testEngineReaderForm = new TestEngineReaderForm();
        testEngineReaderForm.setTestEngine(testEngineForm);
        List<TestEngineReaderVo> testEngineReaderList = testEngineReaderMapper.findList(testEngineReaderForm);
        testEngineVo.setReaderList(testEngineReaderList);
        return testEngineVo;

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void updateStatus(TestEngineForm testEngineForm) throws FrameworkRuntimeException {
        UserVo userVo = AuthCasClient.getUser();
        Date date = new Date();
        testEngineForm.setUpdateUser(userVo);
        testEngineForm.setUpdateDate(date);

        TestEngineVo vo = dao.getById(testEngineForm);
        if (vo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "不存在的测试引擎ID!");
        }

        if (TestEngineVo.START_STATUS.equals(testEngineForm.getStatus())) {
            testEngineForm.setStatus(TestEngineVo.OFFLINE_STATUS);
            testEngineForm.setId(vo.getId());
            dao.updateStatus(testEngineForm);
            return;
        }

        dao.updateStatus(testEngineForm);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = FrameworkRuntimeException.class)
    public void heartbeat(TestEngineForm form) throws FrameworkRuntimeException {
        checkEngineIp(form.getIp());
        TestEngineVo vo = dao.getById(form);
        if (vo != null && StrUtil.isNotBlank(vo.getStatus())) {
            //如果测试引擎处于离线或启用状态,则变更为在线状态
            if (TestEngineVo.OFFLINE_STATUS.equals(vo.getStatus()) || TestEngineVo.START_STATUS.equals(vo.getStatus())
                    || TestEngineVo.ONLINE_STATUS.equals(vo.getStatus())) {
                form.setStatus(TestEngineVo.ONLINE_STATUS);
                form.setCommDate(new Date());
                dao.update(form);
            }
        }
    }

    @Override
    public TestEngineVo findByIp(TestEngineForm form) throws FrameworkRuntimeException {
        return dao.getByIp(form);
    }

}
