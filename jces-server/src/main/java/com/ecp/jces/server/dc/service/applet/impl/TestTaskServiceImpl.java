package com.ecp.jces.server.dc.service.applet.impl;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.*;
import com.ecp.jces.form.extra.ApduDataForm;
import com.ecp.jces.form.extra.StartTestForm;
import com.ecp.jces.jctool.exception.TlvAnalysisException;
import com.ecp.jces.jctool.util.GPUtil;
import com.ecp.jces.server.dc.mapper.applet.*;
import com.ecp.jces.server.dc.mapper.task.*;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineReaderMapper;
import com.ecp.jces.server.dc.service.applet.TestTaskService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Service
@Transactional(readOnly = true)
public class TestTaskServiceImpl implements TestTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestTaskServiceImpl.class);

    @Autowired
    private TestTaskMapper dao;

    @Autowired
    private TestScheduleMapper testScheduleMapper;

    @Autowired
    private TestBusinessScriptMapper testBusinessScriptMapper;

    @Autowired
    private TestEngineMapper testEngineMapper;

    @Autowired
    private TestTaskMapper testTaskMapper;

    @Autowired
    private AppletMapper appletMapper;

    @Autowired
    private AppletVersionMapper appletVersionMapper;

    @Autowired
    private TestReportMapper testReportMapper;

    @Autowired
    private TestReportApduMapper testReportApduMapper;

    @Autowired
    private TestReportDataMapper testReportDataMapper;

    @Autowired
    private TestEngineReaderMapper testEngineReaderMapper;

    @Autowired
    private TestCheckReportMapper testCheckReportMapper;

    @Autowired
    private AppletExeLoadFileMapper appletExeLoadFileMapper;

    @Autowired
    private AppletExeModuleMapper appletExeModuleMapper;

    @Override
    public List<Map<String, Object>> testTaskCreateCount(TestTaskForm form) throws FrameworkRuntimeException {
        return dao.testTaskCreateCount(form);
    }

    @Override
    public List<Map<String, Object>> testTaskStatusCount(TestTaskForm form) throws FrameworkRuntimeException {
        return dao.testTaskStatusCount(form);
    }

    @Override
    @Transactional(rollbackFor = FrameworkRuntimeException.class)
    public void taskResult(StartTestForm form) throws FrameworkRuntimeException {
        String result = "";
        if (ConstantCode.EXECUTION_TASK_SUCCESS.equals(form.getTaskResult())) {
            result = ConstantCode.APPLET_STATUS_TEST_SUCCESS;
        }
        if (ConstantCode.EXECUTION_TASK_FAIL.equals(form.getTaskResult())) {
            result = ConstantCode.APPLET_STATUS_TEST_FAIL;
        }

        Date date = new Date();

        TestTaskVo testTaskVo = dao.findById(form.getTestTaskId());
        if (testTaskVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "找不到该任务ID");
        }

        //判断是否生成了日志,没有生成日志则把日志置空
        if (StrUtil.isNotBlank(form.getCommonLogPath()) && !form.getCommonLogPath().endsWith(ConstantCode.LOG_SUFFIX_ZIP)) {
            form.setCommonLogPath("");
        }
        if (StrUtil.isNotBlank(form.getCustomizeLogPath()) && !form.getCustomizeLogPath().endsWith(ConstantCode.LOG_SUFFIX_ZIP)) {
            form.setCustomizeLogPath("");
        }

        //预测试
        if (ConstantCode.TEST_CONTENT_BUSINESS.equals(form.getTestContent())) {
            handleBusinessScript(form, testTaskVo, date, result);
        }
        //单单是通用测试
        else if (ConstantCode.TEST_CONTENT_GENERAL.equals(form.getTestContent()) || ConstantCode.TEST_CONTENT_PARAM.equals(form.getTestContent())) {
            handleCommonScript(form, testTaskVo, date, result);
        }//都有
        else if (ConstantCode.TEST_CONTENT_ALL.equals(form.getTestContent())) {
            handleAllScript(form, testTaskVo, date);
        }
    }

    @Override
    @Transactional(rollbackFor = FrameworkRuntimeException.class)
    public void progress(Map<String, Object> form) throws FrameworkRuntimeException {
        String testScheduleId = String.valueOf(form.get(ConstantCode.EXECUTION_PARAM_TESTSCHEDULE_ID));
        String testTaskId = String.valueOf(form.get(ConstantCode.EXECUTION_PARAM_TESTTASK_ID));
        TestReportVo testReportVo = testReportMapper.findByTestScheduleId(testScheduleId);
        TestReportForm testReportForm = new TestReportForm();
        Date date = new Date();
        boolean flag = false;
        if (form.get(ConstantCode.EXECUTION_PARAM_LOADDATA) != null) {
            String loadData = form.get(ConstantCode.EXECUTION_PARAM_LOADDATA).toString();
            String[] loadDataArr = loadData.split(",");
            List<TestReportDataForm> loadDataList = new ArrayList<>();
            for (String load : loadDataArr) {
                TestReportDataForm testReportDataForm = new TestReportDataForm();
                String[] loadArr = load.split(":");
                if (loadArr.length == 3) {
                    testReportDataForm.setTaskId(testTaskId);
                    testReportDataForm.setScheduleId(testScheduleId);
                    testReportDataForm.setCapName(loadArr[0]);
                    testReportDataForm.setC6("NaN".equals(loadArr[2]) ? 0 :Integer.parseInt(loadArr[2]));
                    testReportDataForm.setC7("NaN".equals(loadArr[1]) ? 0 :Integer.parseInt(loadArr[1]));
                    testReportDataForm.setC8("NaN".equals(loadArr[2]) ? 0 :Integer.parseInt(loadArr[2]));
                    testReportDataForm.setCreateDate(date);
                    loadDataList.add(testReportDataForm);
                }
            }
            testReportDataMapper.addLoadBatch(loadDataList);

            testReportForm.setLoadC8(loadDataList.stream().mapToInt(TestReportDataForm::getC8).sum());
            testReportForm.setLoadC7(loadDataList.stream().mapToInt(TestReportDataForm::getC7).sum());
            testReportForm.setLoadC6(testReportForm.getLoadC8());
            flag = true;
        }
        if (form.get(ConstantCode.EXECUTION_PARAM_INSTALLDATA) != null) {
            String installData = form.get(ConstantCode.EXECUTION_PARAM_INSTALLDATA).toString();
            String[] installDataArr = installData.split(",");
            List<TestReportDataForm> installDataList = new ArrayList<>();
            for (String install : installDataArr) {
                TestReportDataForm testReportDataForm = new TestReportDataForm();
                String[] installArr = install.split(":");
                if (installArr.length == 3) {
                    testReportDataForm.setTaskId(testTaskId);
                    testReportDataForm.setScheduleId(testScheduleId);
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
            flag = true;


        }
        if (form.get(ConstantCode.EXECUTION_PARAM_RATE) != null) {
            LOGGER.info("单次测试进度：" + form.get(ConstantCode.EXECUTION_PARAM_RATE));
        }

        if (form.get(ConstantCode.EXECUTION_PARAM_DOWNLOAD) != null) {
            String downLoad = form.get(ConstantCode.EXECUTION_PARAM_DOWNLOAD).toString();
            String[] downLoadArr = downLoad.split(",");
            List<ApduDataForm> downLoadList = new ArrayList<>();
            apduListHandle(downLoadArr, downLoadList);

            if (downLoadList.size() > 0) {
                TestReportApduForm testReportApduForm = new TestReportApduForm();
                testReportApduForm.setTestScheduleId(testScheduleId);
                testReportApduForm.setType(ConstantCode.EXECUTION_PARAM_DOWNLOAD);
                testReportApduForm.setList(downLoadList);
                testReportApduMapper.add(testReportApduForm);

                Optional<ApduDataForm> max = downLoadList.stream().max(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setDownloadMaxTime(new BigDecimal(max.get().getTime()).setScale(3, RoundingMode.HALF_UP));
                testReportForm.setDownloadAllTime(new BigDecimal(downLoadList.stream().mapToDouble(ApduDataForm::getTime).sum()).setScale(3, RoundingMode.HALF_UP));

                Optional<ApduDataForm> min = downLoadList.stream().min(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setDownloadMinTime(new BigDecimal(min.get().getTime()).setScale(3, RoundingMode.HALF_UP));

                flag = true;
            }


        }

        if (form.get(ConstantCode.EXECUTION_PARAM_INSTALL) != null) {
            String install = form.get(ConstantCode.EXECUTION_PARAM_INSTALL).toString();
            String[] installArr = install.split(",");
            List<ApduDataForm> installList = new ArrayList<>();
            apduListHandle(installArr, installList);

            if (installList.size() > 0) {
                TestReportApduForm testReportApduForm = new TestReportApduForm();
                testReportApduForm.setTestScheduleId(testScheduleId);
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

        if (form.get(ConstantCode.EXECUTION_PARAM_UNLOAD) != null) {
            String unload = form.get(ConstantCode.EXECUTION_PARAM_UNLOAD).toString();
            String[] unloadArr = unload.split(",");
            List<ApduDataForm> unloadList = new ArrayList<>();
            apduListHandle(unloadArr, unloadList);

            if (unloadList.size() > 0) {
                TestReportApduForm testReportApduForm = new TestReportApduForm();
                testReportApduForm.setTestScheduleId(testScheduleId);
                testReportApduForm.setType(ConstantCode.EXECUTION_PARAM_UNLOAD);
                testReportApduForm.setList(unloadList);
                testReportApduMapper.add(testReportApduForm);

                Optional<ApduDataForm> max = unloadList.stream().max(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setUnloadMaxTime(new BigDecimal(max.get().getTime()).setScale(3, RoundingMode.HALF_UP));
                testReportForm.setUnloadAllTime(new BigDecimal(unloadList.stream().mapToDouble(ApduDataForm::getTime).sum()).setScale(3, RoundingMode.HALF_UP));

                Optional<ApduDataForm> min = unloadList.stream().min(Comparator.comparingDouble(ApduDataForm::getTime));
                testReportForm.setUnloadMinTime(new BigDecimal(min.get().getTime()).setScale(3, RoundingMode.HALF_UP));

                flag = true;
            }
        }

        if (flag) {
            testReportForm.setTestScheduleId(testScheduleId);
            updateOrAddTestReport(testReportVo, testReportForm);
        }
    }

    private void apduListHandle(String[] unloadArr, List<ApduDataForm> list) {
        if (unloadArr.length == 0) {
            return;
        }

        for (String unloadData : unloadArr) {
            ApduDataForm apduTimeForm = new ApduDataForm();
            String[] apduArr = unloadData.split(":");
            if (apduArr.length == 2) {
                apduTimeForm.setApdu(apduArr[0]);
                apduTimeForm.setTime(Double.parseDouble(apduArr[1]));
                list.add(apduTimeForm);
            }
        }
    }

    @Override
    public int count(TestTaskForm form) throws FrameworkRuntimeException {
        return dao.count(form);
    }

    @Override
    public List<TestReportDataVo> dataDetail(TestScheduleForm form) throws FrameworkRuntimeException {
        TestReportDataForm testReportDataForm = new TestReportDataForm();
        testReportDataForm.setScheduleId(form.getId());
        return testReportDataMapper.list(testReportDataForm);
    }

    @Override
    public List<TestReportDataVo> installDetail(TestScheduleForm form) throws FrameworkRuntimeException {
        TestReportDataForm testReportDataForm = new TestReportDataForm();
        testReportDataForm.setScheduleId(form.getId());
        return testReportDataMapper.installList(testReportDataForm);
    }


    /**
     * 处理预测试
     *
     * @param form
     * @param testTaskVo
     * @param date
     * @param result
     */
    private void handleBusinessScript(StartTestForm form, TestTaskVo testTaskVo, Date date, String result) {
        TestTaskForm testTaskForm = new TestTaskForm();
        testTaskForm.setStatus(result);
        testTaskForm.setId(form.getTestTaskId());
        testTaskForm.setTestEnd(date);


        TestScheduleForm testScheduleForm = new TestScheduleForm();
        testScheduleForm.setId(form.getTestScheduleId());
        testScheduleForm.setStatus(result);
        testScheduleForm.setTestEnd(date);


        //变更自定义脚本状态
        TestBusinessScriptForm testBusinessScriptForm = new TestBusinessScriptForm();
        testBusinessScriptForm.setErrorInfo(form.getErrorMsg());
        testBusinessScriptForm.setId(testTaskVo.getTestBusinessScriptId());
        testBusinessScriptForm.setLogPath(form.getCustomizeLogPath());
        testBusinessScriptForm.setTestEnd(date);
        testBusinessScriptForm.setStatus(result.equals(ConstantCode.APPLET_STATUS_TEST_SUCCESS) ?
                ConstantCode.SCRIPT_STATUS_TEST_SUCCESS : ConstantCode.SCRIPT_STATUS_TEST_FAIL);

        //变更测试引擎状态
        TestEngineForm testEngineForm = new TestEngineForm();
        testEngineForm.setId(form.getTestEngineVo().getId());
        testEngineForm.setStatus(TestEngineVo.ONLINE_STATUS);

        testBusinessScriptMapper.edit(testBusinessScriptForm);
        dao.edit(testTaskForm);
        testScheduleMapper.edit(testScheduleForm);
        testEngineMapper.updateStatus(testEngineForm);

        //改变读卡器状态为插入
        if (form.getTestEngineReaderVo() != null) {
            testEngineReaderMapper.changeStatusById(
                    form.getTestEngineReaderVo().getId(), TestEngineReaderVo.INSERT_STATUS);
        }
    }

    /**
     * 处理不包含业务脚本的测试
     *
     * @param form
     * @param testTaskVo
     * @param date
     * @param result
     */
    private void handleCommonScript(StartTestForm form, TestTaskVo testTaskVo, Date date, String result) {

        TestScheduleForm testScheduleForm = new TestScheduleForm();
        testScheduleForm.setId(form.getTestScheduleId());
        testScheduleForm.setStatus(result);
        testScheduleForm.setTestEnd(date);
        testScheduleMapper.edit(testScheduleForm);

        //变更测试引擎状态
        TestEngineForm testEngineForm = new TestEngineForm();
        testEngineForm.setId(form.getTestEngineVo().getId());
        testEngineForm.setStatus(TestEngineVo.ONLINE_STATUS);
        testEngineMapper.updateStatus(testEngineForm);

        //判断该测试任务是否全部卡都已经测完
        TestScheduleForm scheduleForm = new TestScheduleForm();
        scheduleForm.setTestTaskId(testTaskVo.getId());
        List<TestScheduleVo> list = testScheduleMapper.list(scheduleForm);
        updateData(form, testTaskVo, list);

        //更新测试报告
        TestReportVo testReportVo = testReportMapper.findByTestScheduleId(form.getTestScheduleId());
        TestReportForm testReportForm = new TestReportForm();
        testReportForm.setAppletId(testTaskVo.getAppletId());
        testReportForm.setAppletVersionId(testTaskVo.getAppletVersionId());
        testReportForm.setTestScheduleId(form.getTestScheduleId());
        testReportForm.setTestCardId(form.getTestCardVo().getId());
        testReportForm.setResult(ConstantCode.APPLET_STATUS_TEST_SUCCESS.equals(result) ? ConstantCode.TEST_REPORT_RESULT_SUCCESS : ConstantCode.TEST_REPORT_RESULT_FAIL);
        testReportForm.setLogPath(form.getCommonLogPath());
        testReportForm.setDelFlg(ResultCode.NOT_DEL);
        testReportForm.setErrorInfo(form.getErrorMsg());
        updateOrAddTestReport(testReportVo, testReportForm);

        //改变读卡器状态为插入
        if (form.getTestEngineReaderVo() != null) {
            testEngineReaderMapper.changeStatusById(
                    form.getTestEngineReaderVo().getId(), TestEngineReaderVo.INSERT_STATUS);
        }
    }

    /**
     * 处理业务和公共脚本
     *
     * @param form
     * @param testTaskVo
     * @param date
     */
    private void handleAllScript(StartTestForm form, TestTaskVo testTaskVo, Date date) {

        String result;
        if (ResultCode.Success.equals(form.getCommonResult()) && ResultCode.Success.equals(form.getCustomizeResult())) {
            result = ConstantCode.APPLET_STATUS_TEST_SUCCESS;
        } else {
            result = ConstantCode.APPLET_STATUS_TEST_FAIL;
        }
        TestScheduleForm testScheduleForm = new TestScheduleForm();
        testScheduleForm.setId(form.getTestScheduleId());
        testScheduleForm.setStatus(result);
        testScheduleForm.setTestEnd(date);
        testScheduleMapper.edit(testScheduleForm);

        //变更测试引擎状态
        TestEngineForm testEngineForm = new TestEngineForm();
        testEngineForm.setId(form.getTestEngineVo().getId());
        testEngineForm.setStatus(TestEngineVo.ONLINE_STATUS);
        testEngineMapper.updateStatus(testEngineForm);

        //判断该测试任务是否全部卡都已经测完
        TestScheduleForm scheduleForm = new TestScheduleForm();
        scheduleForm.setTestTaskId(testTaskVo.getId());
        List<TestScheduleVo> list = testScheduleMapper.list(scheduleForm);
        updateData(form, testTaskVo, list);

        //更新测试报告
        TestReportVo testReportVo = testReportMapper.findByTestScheduleId(form.getTestScheduleId());
        TestReportForm testReportForm = new TestReportForm();
        testReportForm.setAppletId(testTaskVo.getAppletId());
        testReportForm.setAppletVersionId(testTaskVo.getAppletVersionId());
        testReportForm.setTestScheduleId(form.getTestScheduleId());
        testReportForm.setTestCardId(form.getTestCardVo().getId());
        testReportForm.setResult(ResultCode.Success.equals(form.getCommonResult()) ? ConstantCode.TEST_REPORT_RESULT_SUCCESS : ConstantCode.TEST_REPORT_RESULT_FAIL);
        testReportForm.setLogPath(form.getCommonLogPath());
        testReportForm.setBusinessResult(ResultCode.Success.equals(form.getCustomizeResult()) ? ConstantCode.TEST_REPORT_RESULT_SUCCESS : ConstantCode.TEST_REPORT_RESULT_FAIL);
        testReportForm.setBusinessLogPath(form.getCustomizeLogPath());
        testReportForm.setDelFlg(ResultCode.NOT_DEL);
        testReportForm.setErrorInfo(form.getErrorMsg());
        updateOrAddTestReport(testReportVo, testReportForm);

        //改变读卡器状态为插入
        if (form.getTestEngineReaderVo() != null) {
            testEngineReaderMapper.changeStatusById(
                    form.getTestEngineReaderVo().getId(), TestEngineReaderVo.INSERT_STATUS);
        }
    }

    private void updateOrAddTestReport(TestReportVo testReportVo, TestReportForm testReportForm) {
        Date date = new Date();
        if (testReportVo != null) {
            testReportForm.setId(testReportVo.getId());
            testReportForm.setUpdateDate(date);
            testReportMapper.edit(testReportForm);
        } else {
            testReportForm.setId(StrUtil.newGuid());
            testReportForm.setDelFlg(ResultCode.NOT_DEL);
            testReportForm.setCreateDate(date);
            testReportMapper.add(testReportForm);
        }
    }

    private boolean checkTestReportVo(TestReportVo reportVo, TestCheckReportVo testCheckReportVo) {
        try {
//            if (report.getLoadC7() > testCheckReportVo.getLoadC7MaxExpect()) {
//                return false;
//            }

            if (reportVo.getLoadC8() > testCheckReportVo.getLoadC8MaxExpect()) {
                return false;
            }

            if (reportVo.getC7() > testCheckReportVo.getInstallC7MaxExpect()) {
                return false;
            }

            if (reportVo.getC8() > testCheckReportVo.getInstallC8MaxExpect()) {
                return false;
            }

            if (reportVo.getDownloadMaxTime().intValue() > testCheckReportVo.getDownloadMaxTimeExpect()) {
                return false;
            }

            if (reportVo.getInstallMaxTime().intValue() > testCheckReportVo.getDownloadMaxTimeExpect()) {
                return false;
            }

            if (reportVo.getUnloadMaxTime().intValue() > testCheckReportVo.getDownloadMaxTimeExpect()) {
                return false;
            }

            if ((reportVo.getDownloadAllTime().intValue() + reportVo.getInstallAllTime().intValue()) > testCheckReportVo.getDownloadInstallTimeExpect()) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    private boolean checkReport(StartTestForm form, TestTaskVo testTaskVo) {

        //应用检测，预期值检查
        TestCheckReportVo testCheckReportVo = testCheckReportMapper.findByAppletIdAndVersionId(form.getAppletId(), form.getAppletVersionId());
        if (testCheckReportVo == null) {
            return false;
        }

        if (ConstantCode.TEST_CONTENT_GENERAL.equals(form.getTestContent())) {
            // 卡内存
            if (testCheckReportVo.getMemoryDtrSize() > testCheckReportVo.getMemoryDtrSizeExpect()) {
                return false;
            }
            if (testCheckReportVo.getMemoryRtrSize() > testCheckReportVo.getMemoryRtrSizeExpect()) {
                return false;
            }
            if (testCheckReportVo.getMemoryCodeSize() > testCheckReportVo.getMemoryCodeSizeExpect()) {
                return false;
            }

            // install 过程 new 对象分析（new 对象过多影响应用安装性能）
            if (testCheckReportVo.getInstallNewAmount() > testCheckReportVo.getInstallNewAmountExpect()) {
                return false;
            }
            if (testCheckReportVo.getInstallNewSpace() > testCheckReportVo.getInstallNewSpaceExpect()) {
                return false;
            }
            if (testCheckReportVo.getInstallNewArrayAmount() > testCheckReportVo.getInstallNewArrayAmountExpect()) {
                return false;
            }
            if (testCheckReportVo.getInstallNewArraySpace() > testCheckReportVo.getInstallNewArraySpaceExpect()) {
                return false;
            }

            //静态变量分析
            if (testCheckReportVo.getStaticPrimitiveDefaultAmount() > testCheckReportVo.getStaticPrimitiveDefaultAmountExpect()) {
                return false;
            }
            if (testCheckReportVo.getStaticPrimitiveDefaultSpace() > testCheckReportVo.getStaticPrimitiveDefaultSpaceExpect()) {
                return false;
            }

            if (testCheckReportVo.getStaticPrimitiveNonDefaultAmount() > testCheckReportVo.getStaticPrimitiveNonDefaultAmountExpect()) {
                return false;
            }
            if (testCheckReportVo.getStaticPrimitiveNonDefaultSpace() > testCheckReportVo.getStaticPrimitiveNonDefaultSpaceExpect()) {
                return false;
            }

            if (testCheckReportVo.getStaticReferenceNullAmount() > testCheckReportVo.getStaticReferenceNullAmountExpect()) {
                return false;
            }
            if (testCheckReportVo.getStaticReferenceNullSpace() > testCheckReportVo.getStaticReferenceNullSpaceExpect()) {
                return false;
            }

            if (testCheckReportVo.getStaticReferenceArrayInitAmount() > testCheckReportVo.getStaticReferenceArrayInitAmountExpect()) {
                return false;
            }
            if (testCheckReportVo.getStaticReferenceArrayInitSpace() > testCheckReportVo.getStaticReferenceArrayInitSpaceExpect()) {
                return false;
            }

            /*
            // 非标API检测
            if (!StringUtil.isEmpty(testCheckReportVo.getNonstandardApi())) {
                return false;
            }
            // 敏感API检测
            if (!StringUtil.isEmpty(testCheckReportVo.getSensitiveApi())) {
                return false;
            }
            // 应用调用禁用注册toolkit事件
            if (!StringUtil.isEmpty(testCheckReportVo.getEventList())) {
                return false;
            }
             */
        }


        // 兼容性测试预期值检查
        List<TestReportVo> reportList;
        if (form.getTestContent() == ConstantCode.TEST_CONTENT_PARAM) {
            reportList = testReportMapper.listParamByTaskId(testTaskVo.getId());
        } else {
            reportList = testReportMapper.listCommByTaskId(testTaskVo.getId());
        }

        for (TestReportVo report : reportList) {
            if (!checkTestReportVo(report, testCheckReportVo)) {
                return false;
            }
        }

        return true;
    }

    private void updateData(StartTestForm form, TestTaskVo testTaskVo, List<TestScheduleVo> list) {
        if (list.size() > 0) {
            int overCount = 0;
            int successCount = 0;

            int commTestCount = 0;
            int commTestOverCount = 0;
            int commTestSuccessCount = 0;
            for (TestScheduleVo vo : list) {
                if (ConstantCode.TEST_CONTENT_GENERAL.equals(vo.getTestContent())) {
                    commTestCount++;

                    if (ConstantCode.APPLET_STATUS_TEST_SUCCESS.equals(vo.getStatus()) ||
                            ConstantCode.APPLET_STATUS_TEST_FAIL.equals(vo.getStatus())) {
                        commTestOverCount++;
                    }
                    if (ConstantCode.APPLET_STATUS_TEST_SUCCESS.equals(vo.getStatus())) {
                        commTestSuccessCount++;
                    }
                }

                if (ConstantCode.APPLET_STATUS_TEST_SUCCESS.equals(vo.getStatus()) ||
                        ConstantCode.APPLET_STATUS_TEST_FAIL.equals(vo.getStatus())) {
                    overCount++;
                }
                if (ConstantCode.APPLET_STATUS_TEST_SUCCESS.equals(vo.getStatus())) {
                    successCount++;
                }
            }

            if (commTestCount == commTestOverCount || overCount == list.size()) {
                String res;
                if (ConstantCode.TEST_CONTENT_GENERAL.equals(form.getTestContent())) {
                    if (commTestCount > 0 && commTestSuccessCount == commTestCount) {
                        if (checkReport(form, testTaskVo)) { // 检查测试结果,如果通过做参数校验测试
                            try {
                                //更新新的加载参数和安装参数
                                updateParam(testTaskVo.getId(), form.getLoadFiles());
                            } catch (Exception ex) {
                                LOGGER.error("更新新的加载参数和安装参数出错", ex);
                            }

                            testScheduleMapper.changeStatus(form.getTestTaskId());
                            return;
                        } else {
                            res = ConstantCode.APPLET_STATUS_TEST_FAIL;
                        }
                    } else {
                        res = ConstantCode.APPLET_STATUS_TEST_FAIL;
                    }
                } else {
                    if (successCount == list.size()) {
                        if (checkReport(form, testTaskVo)) { // 检查测试结果,如果通过做参数校验测试
                            res = ConstantCode.APPLET_STATUS_TEST_SUCCESS;
                        } else {
                            res = ConstantCode.APPLET_STATUS_TEST_FAIL;
                        }
                    } else {
                        res = ConstantCode.APPLET_STATUS_TEST_FAIL;
                    }
                }

                //修改测试任务表
                TestTaskForm testTaskForm = new TestTaskForm();
                testTaskForm.setId(testTaskVo.getId());
                testTaskForm.setStatus(res);
                testTaskForm.setTestEnd(new Date());
                testTaskMapper.edit(testTaskForm);

                AppletForm appletForm = new AppletForm();
                appletForm.setId(form.getAppletId());
                appletForm.setStatus(res);
                appletMapper.edit(appletForm);

                AppletVersionForm appletVersionForm = new AppletVersionForm();
                appletVersionForm.setId(testTaskVo.getAppletVersionId());
                appletVersionForm.setStatus(res);
                appletVersionMapper.edit(appletVersionForm);
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
                    if(StrUtil.isNotBlank(installParam)&&ConstantCode.C900.equalsIgnoreCase(installParam)){
                        installParam = "";
                    }
                    dataMap.put(dataVo.getCapName(), GPUtil.genInstallForInstallParam(dataVo.getC7(), dataVo.getC8(), installParam));
                } catch (TlvAnalysisException e) {
                    LOGGER.error(e.getMessage(), e);
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
    public List<TestScheduleVo> findByTestBusinessScriptId(TestTaskForm form){
        List<TestScheduleVo> list = testScheduleMapper.findByTestTaskId(form);
        if(list.size() == 0){
            return null;
        }
        for(TestScheduleVo scheduleVo : list){
            if(StrUtil.isNotBlank(scheduleVo.getBusinessLogPath())){
                scheduleVo.setBusinessLogPath(AesUtil2.encryptData(scheduleVo.getBusinessLogPath()));
            }
        }
        return list;
    }
}
