package com.ecp.jces.server.schedule;

import com.ecp.jces.server.dc.mapper.terminal.TestEngineMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 测试引擎离线检测
 *
 * @author kangjunrong
 */
@Component
public class EngineOffLineSchedules {
    private static final Logger logger = LogManager.getFormatterLogger(EngineOffLineSchedules.class);
    @Autowired
    private TestEngineMapper testEngineMapper;

    private final static int OffLineTime = 10;

    @Scheduled(initialDelay = 10 * 1000, fixedRate = 5000)
    public void init() {
        try {
            //主动监测所有在线测试引擎
            testEngineMapper.checkOffLine(OffLineTime);
            //logger.info("矩阵离线检测成功");
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("测试引擎离线检测出错------------------------");
        }
    }


}

