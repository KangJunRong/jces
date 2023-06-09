package com.ecp.jces.local;


import com.ecp.jces.code.ResultCode;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.vo.UserVo;
import com.ecp.jces.exception.FrameworkRuntimeException;

import java.util.Date;

/**
 * 注释说明
 */
public class AuthCasClient {

    private final static ThreadLocal<LocalObj> threadSession = new ThreadLocal<LocalObj>();

    public final static void add(UserVo user) throws FrameworkRuntimeException {
        LocalObj obj = new LocalObj();
        obj.setUser(user);
        obj.setCurr(new Date());
        threadSession.set(obj);
    }

    public final static LocalObj get() throws FrameworkRuntimeException {
        return threadSession.get();
    }

    public final static UserVo getUser() throws FrameworkRuntimeException {
        if (threadSession.get() != null) {
            return threadSession.get().getUser();
        }
        // 非空检验
        throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "用户没登录");
    }

    public final static String getUserId() throws FrameworkRuntimeException {
        if (threadSession.get() != null) {
            return getUser().getId();
        }
        throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "用户没登录");
    }

    public final static Date getCurr() throws FrameworkRuntimeException {
        if (threadSession.get() != null) {
            // 返回统一请求时间
            return threadSession.get().getCurr();
        }
        // 非空检验
        throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "用户没登录");
    }
}
