package com.ecp.jces.server.util;

import com.alibaba.fastjson.JSON;
import com.ecp.jces.auth.AuthJwt;
import com.ecp.jces.code.ThirdCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JWTUtils {

    private JWTUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger logger = LogManager.getFormatterLogger(JWTUtils.class);

    private static final String USER_ID = "userId";
    private static final String TICKET = "ticket";
    private static final String AUTH_KEY = "authKey";

    public static final String create(String userId, String ticket, int timeout, String authKey) {
        Map<String, String> params = new HashMap<>(3);
        params.put(USER_ID, userId);
        params.put(TICKET, ticket);
        params.put(AUTH_KEY, authKey);
        return Jwts.builder().setSubject(JSON.toJSONString(params))
                .setExpiration(new Date(System.currentTimeMillis() + timeout * 1000))
                .signWith(SignatureAlgorithm.HS512, ThirdCode.SIGN).compact();
    }

    /**
     * 登录jwt校验
     */
    public static final AuthJwt verify(String authorization) {
        if (StringUtils.isBlank(authorization)) {
            return null;
        }
        try {
            String subject = Jwts.parser().setSigningKey(ThirdCode.SIGN).parseClaimsJws(authorization)
                    .getBody().getSubject();
            if (StringUtils.isNotBlank(subject)) {
                Map<String, String> map = JSON.parseObject(subject, Map.class);
                String userId = map.get(USER_ID);
                String ticket = map.get(TICKET);
                String authKey = map.get(AUTH_KEY);
                return new AuthJwt(userId, ticket, false,authKey);
            }
        } catch (ExpiredJwtException e) {
            // 刷新
            String subject = e.getClaims().getSubject();
            Map<String, String> map = JSON.parseObject(subject, Map.class);
            String userId = map.get(USER_ID);
            String ticket = map.get(TICKET);
            String authKey = map.get(AUTH_KEY);
            return new AuthJwt(userId, ticket, true,authKey);
        } catch (SignatureException e) {
            // 校验签名失败和无法解析
            logger.error(e);
        }
        return null;
    }


}
