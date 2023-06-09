package com.ecp.jces.server.filter;

import com.alibaba.fastjson.JSONException;
import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.exception.BaseExceptionConstants;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.global.LogMsg;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.config.Config;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.service.user.UserService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.UserVo;
import com.sun.org.apache.regexp.internal.RE;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class AccessLogFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);
    private static final String OPTION = "OPTION";

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private UserService userService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        String requestURI = null;
        Long start = System.currentTimeMillis();
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");
        String method = request.getMethod();
        method = method.toUpperCase();
        if (method.contains(OPTION)) {
            return;
        }
        //记录访问地址
        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        try {
            // 不需要权限校验
            requestURI = request.getRequestURI();
            // 处理auth的鉴权跳过
            if (requestURI.equals("/jces-server/user/register")) {
                chain.doFilter(request, response);
            } else if (requestURI.equals("/jces-server/apiForbiddenSave/list")) {
                chain.doFilter(request, response);
            } else if (requestURI.equals("/jces-server/engine/register")) {
                chain.doFilter(request, response);
            } else if (requestURI.equals("/jces-server/engine/uploadEngineInfo")) {
                chain.doFilter(request, response);
            } else if (requestURI.equals("/jces-server/engine/callbackTesting")) {
                chain.doFilter(request, response);
            } else if (requestURI.equals("/jces-server/engine/callbackStop")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/engine/vmCosResult")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/engine/heartbeat")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/engine/uploadBusinessResult")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/engine/uploadResult")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/tool/fileAuth")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/druid")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/third/capCommit")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/third/capResult")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/third/login")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/third/singleLogin")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/user/login")) {
                if (redisDao.getIpLock(ip) != null) {
                    responseMsg(res, ResultCode.Fail, "登录失败次数过多,IP锁定中,请稍候登录");
                    return;
                }
                Integer count = redisDao.getIpCount(ip);
                if (count == null) {
                    count = 1;
                    redisDao.setIpCount(ip, count);
                    chain.doFilter(request, response);
                    return;
                }
                if (count == 3) {
                    redisDao.setIpLock(ip);
                    responseMsg(res, ResultCode.Fail, "登录失败超过3次,IP锁定5分钟,请稍候登录");
                    return;
                }
                count++;
                redisDao.setIpCount(ip, count);
                chain.doFilter(request, response);
            } else if (requestURI.contains("/user/logout")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/user/alive")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/user/forgetPassword")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/tool/getVerifyCodeImg")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/ide/logUpload")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/tool/checkIp")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/tool/vmCosDownAuth")) {
                chain.doFilter(request, response);
            } else if (requestURI.contains("/cos/getCosChildVersion")) {
                chain.doFilter(request, response);
            } else {
                String userId = request.getHeader("userId");
                String ticket = request.getHeader("ticket");
                if (StrUtil.isBlank(userId) || StrUtil.isBlank(ticket)) {
                    responseMsg(res, ResultCode.ParamIllegal, "用户没登陆");
                    return;
                }

                String webTicket = redisDao.getWebUserTicket(userId);
                if (webTicket == null) {
                    responseMsg(res, ResultCode.TimeOut, "登录超时");
                    return;
                }

                if (!ticket.equals(webTicket)) {
                    responseMsg(res, ResultCode.TokenError, "账号再别处登录，当前登录被踢出");
                    return;
                }

                UserVo user = userService.findById(userId);
                if (user == null) {
                    responseMsg(res, ResultCode.ParamIllegal, "用户不存在");
                    return;
                }
                if (ResultCode.DEL == user.getDelFlg()) {
                    responseMsg(res, ResultCode.ParamIllegal, "账号已被删除");
                    return;
                }
                if (ConstantCode.USER_STATUS_DISABLED.equals(user.getStatus())) {
                    responseMsg(res, ResultCode.ParamIllegal, "账号已被停用");
                    return;
                }
                //鉴权
                if (!ConstantCode.ROLE_ADMINISTRATORS.equals(user.getRoleId())) {
                    if (!requestURI.contains(ConstantCode.COMMIT_TEST_URI)
                            || !StrUtil.isNotBlank(user.getTemplateId())) {
                        if (!Config.DevPermission.contains(requestURI)) {
                            LOGGER.error(requestURI + " 开发者没有这权限: " + user.getAccount());
                            //垂直越权
                            responseMsg(res, ResultCode.PermissionIllegal, "越权访问");
                            return;
                        }
                    }
                }

                //注释了就是不管有没有操作都是登录一小时后超时
                //有操作，不超时,1小时不操作超时
                //redisDao.setWebUserTicket(userId, ticket);

                AuthCasClient.add(user);
                chain.doFilter(request, response);
            }
        } catch (JSONException e) {
            LOGGER.error(e.toString());
            responseMsg(res, ResultCode.Fail,
                    BaseExceptionConstants.getMessage(BaseExceptionConstants.JSON_PROCESSING_EXCEPTION));
        } catch (FrameworkRuntimeException e) {
            LOGGER.error(e.toString());
            responseMsg(res, ResultCode.Fail, e.getMessage());
        } catch (Exception e) {
            if (e.getCause() instanceof FrameworkRuntimeException) {
                FrameworkRuntimeException ex = (FrameworkRuntimeException) e.getCause();
                responseMsg(res, ResultCode.Fail, ex.getMessage());
            } else {
                LOGGER.error(e.toString());
                LOGGER.error(LogMsg.to("requestURI", requestURI));
                responseMsg(res, ResultCode.Fail,
                        BaseExceptionConstants.getMessage(BaseExceptionConstants.BASE_ERROR));
            }
        } finally {
            Map<String, String[]> query = request.getParameterMap();
            String queryStr = null;
            if (query != null && query.size() > 0) {
                queryStr = JSONUtils.toJSONString(query);
            }

            if (!requestURI.contains("heartbeat") && requestURI.contains("engine")) {
                LOGGER.info("<{}>[{}][{}] 总体耗时：{} |" +
                                "query 参数 {} ", ip, requestURI, method, (System.currentTimeMillis() - start),
                        queryStr);
            }
            /*LOGGER.info("<{}>[{}][{}] 总体耗时：{} |" +
                            "query 参数 {} ", ip, requestURI, method, (System.currentTimeMillis() - start),
                    queryStr);*/
        }
    }

    private static void responseMsg(ServletResponse res, String code, String msg) {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Code", String.valueOf(code));

        String curr = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
        response.addHeader("Resp-Time", curr);
        response.setStatus(HttpServletResponse.SC_OK);

        Map<Object, Object> params = new HashMap<>(3);
        params.put("code", code);
        params.put("msg", msg);
        try (PrintWriter out = response.getWriter()) {
            out.append(JSONUtils.toJSONString(params));
        } catch (IOException e) {
            LOGGER.error(LogMsg.to("ex", e));
        }
    }
}