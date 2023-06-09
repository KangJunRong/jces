package com.ecp.jces.server.schedule;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.AppletForm;
import com.ecp.jces.form.TestMatrixForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.form.extra.StartTestForm;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.mapper.applet.AppletMapper;
import com.ecp.jces.server.dc.mapper.applet.AppletVersionMapper;
import com.ecp.jces.server.dc.mapper.task.TestTaskMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixMapper;
import com.ecp.jces.server.dc.service.async.AsyncService;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.vo.AppletExeLoadFileVo;
import com.ecp.jces.vo.MsgEntityVo;
import com.ecp.jces.vo.TestMatrixVo;
import com.ecp.jces.vo.TestTaskVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author kangjunrong
 */
@Component
public class TaskSchedules {

    private static final Logger logger = LogManager.getFormatterLogger(TaskSchedules.class);

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private TestMatrixMapper testMatrixMapper;
    @Autowired
    private CenterInf centerInf;
    @Autowired
    private AppletVersionMapper appletVersionMapper;
    @Autowired
    private TestTaskMapper testTaskMapper;
    @Autowired
    private AppletMapper appletMapper;

    @Autowired
    private RedisDao redisDao;
    private final String scheduledLock = "jces:ScheduledLock";
    //private String defaultInstallParam = "c900";

    /**
     * 调度中心-定时任务
     */
    @Scheduled(initialDelay = 5 * 1000, fixedRate = 10000)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void init() {
        //定时任务锁.任务没结束，不能进入任务
        String lock = redisDao.getValue(scheduledLock);
        if (lock != null) {
            logger.info("任务没结束");
            return;
        }
        redisDao.setValueTtl(scheduledLock, "1", 30L);
        try {
            //先查看有没有待测试任务
            TestTaskForm testTaskForm = new TestTaskForm();
            testTaskForm.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
            testTaskForm.setDelFlg(ResultCode.NOT_DEL);
            List<TestTaskVo> testTaskVos = testTaskMapper.list(testTaskForm);
            if (testTaskVos.size() == 0) {
                return;
            }

            //再查看有没有空闲矩阵资源
            TestMatrixForm testMatrixForm = new TestMatrixForm();
            testMatrixForm.setMatrixStatus(TestMatrixVo.FREE_STATUS);
            testMatrixForm.setDelFlg(ResultCode.NOT_DEL);
            List<TestMatrixVo> list = testMatrixMapper.findFreeList(testMatrixForm);

            if (list.size() == 0) {
                logger.info("没有空闲的测试矩阵");
                return;
            }
            Map<String, TestMatrixVo> matrixMap = list.stream().collect(Collectors.toMap(TestMatrixVo::getMatrixId, x -> x));

            int adapterCount = matrixMap.size();
            if (adapterCount > 0) {
                //空闲矩阵消耗完结束
                for (TestTaskVo vo : testTaskVos) {
                    if (matrixMap.size() == 0) {
                        break;
                    }
                    TestMatrixVo testMatrixVo = findMatrixForVersionNo(matrixMap, vo.getVersionNo());
                    if (testMatrixVo == null) {
                        logger.info("找不到[" + vo.getVersionNo() + "]版本的空闲矩阵");
                        continue;
                    }
                    //找到矩阵，下发任务
                    if (sendTestTask(vo, testMatrixVo)) {
                        matrixMap.remove(testMatrixVo.getMatrixId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("定时任务出错------------------------");
        } finally {
            //任务结束,解除锁
            //logger.info("任务结束,解除锁");
            redisDao.delValue(scheduledLock);
        }
    }


    /**
     * 找出对应cos版本的矩阵往下发
     */
    private TestMatrixVo findMatrixForVersionNo(Map<String, TestMatrixVo> matrixMap, String versionNo) {
        for (String key : matrixMap.keySet()) {
            TestMatrixVo vo = matrixMap.get(key);
            if (versionNo.equals(vo.getVersionNo())) {
                return vo;
            }
        }
        return null;
    }

    /**
     * @param vo
     * @param testMatrixVo
     * @return true表示矩阵被使用了
     */
    private boolean sendTestTask(TestTaskVo vo, TestMatrixVo testMatrixVo) {

        redisDao.setValueTtl(ConstantCode.TASK_START + vo.getId(), testMatrixVo.getMatrixId(), 180L);


        //下发逻辑
        String url;
        String testType;
        //预测试
        if (ConstantCode.TEST_CONTENT_BUSINESS.equals(vo.getType())) {
            testType = ConstantCode.TEST_CONTENT_BUSINESS_MSG;
        } else {
            testType = ConstantCode.TEST_CONTENT_GENERAL_MSG;
            if (vo.getParamTest() != null && vo.getParamTest()) {
                testType = ConstantCode.TEST_CONTENT_PARAM_MSG;
            }
        }

        //找cap包和路径
        StartTestForm testForm = appletVersionMapper.findByTaskId(vo.getId());
        if (testForm == null) {
            logger.info("没有找到Cap包,不能测试, TestTaskId : " + vo.getId());
            return false;
        }
        List<AppletExeLoadFileVo> loadFiles = appletMapper.getLoadFiles(testForm.getAppletId(), testForm.getAppletVersionId());

        StartTestForm form = new StartTestForm();
        form.setMatrixId(testMatrixVo.getMatrixId());
        form.setCapPath(AesUtil2.encryptData(testForm.getCapPath()));

        if (ConstantCode.TEST_CONTENT_BUSINESS.equals(vo.getType())) {
            form.setCustomizeScriptPath(AesUtil2.encryptData(testForm.getCustomizeScriptPath()));
        } else {
            form.setCommonScriptPath(AesUtil2.encryptData(testForm.getCommonScriptPath()));
        }

        form.setLoadFiles(loadFiles);
        form.setTestTaskId(vo.getId());
        form.setTestContent(vo.getType());

        //下发测试前先把 java card 测试做了
        boolean flag = true;
        if (!ConstantCode.TEST_CONTENT_BUSINESS.equals(vo.getType()) &&
                !ConstantCode.TEST_CONTENT_PARAM.equals(vo.getType())) {
            flag = centerInf.taskJavaCard(testMatrixVo.getEngineId(), testMatrixVo.getIp(), testMatrixVo.getPort(), form);
        }
        //true才可以下发实卡测试
        if (flag) {
            logger.info("实卡测试");
            MsgEntityVo result = centerInf.taskStart(testType, testMatrixVo.getEngineId(), form);
            if (result != null && ResultCode.Success.equals(result.getCode())) {
                logger.info(testType + " 下发测试成功, TestTaskId : " + vo.getId());

                //安全审核检测
                asyncService.handleSaveApi(testForm);

                //修改测试任务表
                TestTaskForm tForm = new TestTaskForm();
                tForm.setId(vo.getId());
                tForm.setStatus(ConstantCode.APPLET_STATUS_TESTING);
                if (vo.getTestStart() == null) {
                    tForm.setTestStart(new Date());
                }
                tForm.setMatrixId(testMatrixVo.getMatrixId());
                testTaskMapper.editForStart(tForm);

                //预测试不改变应用测试状态
                if (!ConstantCode.TEST_CONTENT_BUSINESS.equals(form.getTestContent())) {
                    AppletForm appletForm = new AppletForm();
                    appletForm.setId(testForm.getAppletId());
                    appletForm.setStatus(ConstantCode.APPLET_STATUS_TESTING);
                    appletMapper.edit(appletForm);
                }

                //改变矩阵状态为测试中
                TestMatrixForm testMatrixForm = new TestMatrixForm();
                testMatrixForm.setMatrixId(testMatrixVo.getMatrixId());
                testMatrixForm.setMatrixStatus(TestMatrixVo.TESTING_STATUS);
                testMatrixMapper.update(testMatrixForm);
            } else {
                logger.info(testType + " 下发测试失败,原因: " + result.getMsg()
                        + ", TestTaskId : " + vo.getId() + " ,等待下次下发");
            }
        }
        return true;
    }
}

