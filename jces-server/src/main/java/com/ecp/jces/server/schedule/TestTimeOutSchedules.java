package com.ecp.jces.server.schedule;


import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.form.AppletForm;
import com.ecp.jces.form.AppletVersionForm;
import com.ecp.jces.form.TestMatrixForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.server.dc.mapper.applet.AppletMapper;
import com.ecp.jces.server.dc.mapper.applet.AppletVersionMapper;
import com.ecp.jces.server.dc.mapper.task.TestTaskMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixMapper;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.vo.TestEngineVo;
import com.ecp.jces.vo.TestMatrixVo;
import com.ecp.jces.vo.TestTaskVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 测试超时终止任务
 *
 * @author kangjunrong
 */
@Component
public class TestTimeOutSchedules {
    private static final Logger logger = LogManager.getFormatterLogger(TestTimeOutSchedules.class);
    @Autowired
    private TestTaskMapper testTaskMapper;
    @Autowired
    private TestEngineMapper testEngineMapper;
    @Autowired
    private CenterInf centerInf;

    @Scheduled(initialDelay = 10 * 1000, fixedRate = 60000)
    public void init() {
        try {
            //找出需要终止测试的任务列表
            List<TestTaskVo> list = testTaskMapper.timeOutList();
            if (list.size() == 0) {
                return;
            }
            for (TestTaskVo vo : list) {
                logger.info("终止测试开始,taskId :" + vo.getId());
                TestEngineVo engineVo = testEngineMapper.findByMatrixId(vo.getMatrixId());
                if (engineVo != null && !TestEngineVo.ONLINE_STATUS.equals(engineVo.getStatus())) {
                    logger.error(engineVo.getName() + "--> 测试引擎不是在线状态不能终止");
                    continue;
                }

                try {
                    assert engineVo != null;
                    centerInf.terminateTest(engineVo, vo.getMatrixId(), vo.getId());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                logger.info("终止测试结束,taskId :" + vo.getId());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("测试超时终止任务出错------------------------");
        }
    }


}

