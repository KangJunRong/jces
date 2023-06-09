package com.ecp.jces.server.util.redislock;

import org.springframework.stereotype.Component;

/**
 * DistributedLock.java 顶级接口
 * @date 2018年9月25日 下午3:11:05
 * @version 1.0.0
 */
@Component
public interface DistributedLock {

    /** 设置超时30秒 **/
    public static final long TIMEOUT_MILLIS = 30000;

    public static final int RETRY_TIMES = Integer.MAX_VALUE;

    /** 设置睡眠0.5秒 **/
    public static final long SLEEP_MILLIS = 500;

    public boolean lock(String key);

    public boolean lock(String key, int retryTimes);

    public boolean lock(String key, int retryTimes, long sleepMillis);

    public boolean lock(String key, long expire);

    public boolean lock(String key, long expire, int retryTimes);

    public boolean lock(String key, long expire, int retryTimes, long sleepMillis);

    public boolean releaseLock(String key);
}