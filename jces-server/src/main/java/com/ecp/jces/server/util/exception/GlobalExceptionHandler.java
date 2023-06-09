package com.ecp.jces.server.util.exception;

import com.ecp.jces.code.ResultCode;

import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.exception.BaseExceptionConstants;
import com.ecp.jces.exception.FrameworkRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 全局系统异常处理
     *
     * @param req
     * @param e
     * @return 错误信息
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String defaultErrorHandler(HttpServletRequest req, Exception e) {
        Map<Object, Object> params = new HashMap<>(3);
        if (e instanceof FrameworkRuntimeException) {
            FrameworkRuntimeException peopleException = (FrameworkRuntimeException) e;
            params.put("code", peopleException.getCode());
            params.put("msg", peopleException.getMsg());
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            params.put("code", ResultCode.Fail);
            params.put("msg", "错误的请求方式");
        } else if (e instanceof NoHandlerFoundException) {
            params.put("code", ResultCode.RequestIllegal);
            params.put("msg", "不存在的路径");
        } else if (e instanceof MissingServletRequestParameterException) {
            params.put("code", ResultCode.Fail);
            params.put("msg", "参数" + ((MissingServletRequestParameterException) e).getParameterName() + "为空");
        } else if (e instanceof MissingServletRequestPartException) {
            params.put("code", ResultCode.Fail);
            params.put("msg", "参数" + ((MissingServletRequestPartException) e).getRequestPartName() + "为空");
        } else {
            logger.error("[全局系统异常]-{}", e);
            params.put("code", BaseExceptionConstants.BASE_ERROR);
            params.put("msg", BaseExceptionConstants.getMessage(BaseExceptionConstants.BASE_ERROR));
        }
        return JSONUtils.toJSONString(params);
    }
}