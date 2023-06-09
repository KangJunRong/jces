package com.ecp.jces.server.dc.cache;

import com.ecp.jces.server.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisDao {


    @Resource
    private RedisTemplate<String, String> objectRedisTemplate;

    private static final Logger log = LoggerFactory.getLogger(RedisDao.class);

    private static final String Ticket = "jces:ticket:web:";
    private long Live_Time = 72 * 60 * 60;

    private static final String ApiTicket = "jces:ticket:api:";
    private static final int ApiTimeOut = 60 * 60 * 24;

    private static final String WaterAppTicket = "jces:ticket:water:";
    private static final int WaterAppOut = 3600 * 24 * 7;

    private static final String JCES_IP = "jces:ip:";
    private static final int JCES_IP_TIMEOUT = 60 * 3;

    public void setWebUserTicket(String userid, String ticket) {
        try {
            if (userid == null || ticket == null) {
                return;
            }
            this.objectRedisTemplate.opsForValue().set(Ticket + userid, ticket, Live_Time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("setWebUserToken", e);
        }
    }

    public String getWebUserTicket(String userid) {
        try {
            if (userid == null) {
                return null;
            }
            return this.objectRedisTemplate.opsForValue().get(Ticket + userid);
        } catch (Exception e) {
            log.error("getWebUserToken", e);
            return null;
        }
    }

    public void delWebUserTicket(String userid) {
        try {
            if (userid == null) {
                return;
            }
            this.objectRedisTemplate.opsForValue().getOperations().delete(Ticket + userid);
        } catch (Exception e) {
            log.error("delWebUserToken", e);
        }
    }

    public void setApiUserTicket(String userid, String token) {
        try {
            if (userid == null || token == null) {
                return;
            }
            this.objectRedisTemplate.opsForValue().set(ApiTicket + userid, token, ApiTimeOut, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("setApiUserToken", e);
        }
    }

    public String getApiUserTicket(String userid) {
        try {
            if (userid == null) {
                return null;
            }
            return this.objectRedisTemplate.opsForValue().get(ApiTicket + userid);

        } catch (Exception e) {
            log.error("getApiUserToken", e);
            return null;
        }
    }

    public void delApiUserTicket(String userid) {
        try {
            if (userid == null) {
                return;
            }
            this.objectRedisTemplate.opsForValue().getOperations().delete(ApiTicket + userid);
        } catch (Exception e) {
            log.error("delApiUserToken", e);
        }
    }


    public void setWaterAppUserTicket(String userid, String token) {
        try {
            if (userid == null || token == null) {
                return;
            }
            this.objectRedisTemplate.opsForValue().set(WaterAppTicket + userid, token, WaterAppOut, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("setWaterAppUserTicket", e);
        }
    }

    public String getWaterAppUserTicket(String userid) {
        try {
            if (userid == null) {
                return null;
            }
            return this.objectRedisTemplate.opsForValue().get(WaterAppTicket + userid);

        } catch (Exception e) {
            log.error("getWaterAppUserTicket", e);
            return null;
        }
    }

    public void setValue(String key, String value) {
        if (StrUtil.isBlank(key) || StrUtil.isBlank(value)) {
            return;
        }
        this.objectRedisTemplate.opsForValue().set(key, value);
    }

    public String getValue(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        return objectRedisTemplate.opsForValue().get(key);
    }

    public void setValueTtl(String key, String value, Long seconds) {
        if (StrUtil.isBlank(key) || StrUtil.isBlank(value) || seconds == null) {
            return;
        }
        this.objectRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    public void delValue(String key) {
        if (StrUtil.isBlank(key)) {
            return;
        }
        this.objectRedisTemplate.opsForValue().getOperations().delete(key);
    }


    public void setTtl(String key, Long seconds) {
        if (StrUtil.isBlank(key) || seconds == null) {
            return;
        }
        this.objectRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public void setIpCount(String ip, Integer count) {
        if (StrUtil.isBlank(ip) || count == null) {
            return;
        }
        this.objectRedisTemplate.opsForValue().set(JCES_IP +ip, String.valueOf(count), JCES_IP_TIMEOUT, TimeUnit.SECONDS);
    }

    public Integer getIpCount(String ip) {
        if (StrUtil.isBlank(ip)) {
            return null;
        }
        String count = objectRedisTemplate.opsForValue().get(JCES_IP + ip);
        if(count == null){
            return null;
        }
        return Integer.parseInt(count);
    }

    public void delIpCount(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        this.objectRedisTemplate.opsForValue().getOperations().delete(JCES_IP + ip);
    }

    public void setIpLock(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        this.objectRedisTemplate.opsForValue().set(JCES_IP + "lock:" +ip, "lock", 300, TimeUnit.SECONDS);
    }

    public String getIpLock(String ip) {
        if (StrUtil.isBlank(ip)) {
            return null;
        }
        return this.objectRedisTemplate.opsForValue().get(JCES_IP + "lock:" +ip);
    }

}
