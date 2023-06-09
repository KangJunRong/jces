package com.ecp.jces.server.schedule;

import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineMapper;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.vo.TestEngineVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 测试引擎定时下发上报命令
 *
 * @author kangjunrong
 */
@Component
public class UpdateEngineInfoSchedules {
    private static final Logger logger = LogManager.getFormatterLogger(UpdateEngineInfoSchedules.class);
    @Autowired
    private TestEngineMapper testEngineMapper;

    @Autowired
    private CenterInf centerInf;

    //每天的5点、13点都执行一次
    @Scheduled(cron = "0 0 5,13 * * ?")
    public void init() {
        try {
            TestEngineForm testEngineForm = new TestEngineForm();
            testEngineForm.setStatus(TestEngineVo.ONLINE_STATUS);
            List<TestEngineVo> list = testEngineMapper.findList(testEngineForm);
            if (list.size() == 0) {
                return;
            }
            for (TestEngineVo vo : list) {
                centerInf.getTestMatrixInformation(vo);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("测试引擎定时下发上报命令出错------------------------");
        }
    }


}

