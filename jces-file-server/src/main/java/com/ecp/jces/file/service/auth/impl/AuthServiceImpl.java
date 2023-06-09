package com.ecp.jces.file.service.auth.impl;

import com.eastcompeace.capAnalysis.doman.API;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.file.service.auth.AuthService;
import com.ecp.jces.file.util.HttpClientUtils;
import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.form.extra.FileAuth;
import com.ecp.jces.form.extra.VmCos;
import com.ecp.jces.vo.MsgEntityVo;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Value("${param.jces.cloudServerUrl}")
    private String cloudServerUrl;

    @Override
    public void auth(FileAuth fileAuth) throws FrameworkRuntimeException {

        String url = cloudServerUrl + "tool/fileAuth";
        MsgEntityVo msgEntityVo;
        try {
            //设置请求头信息
            HttpPost httpPost = new HttpPost(url);
            msgEntityVo = HttpClientUtils.post(httpPost, JSONUtils.toJSONString(fileAuth), 2000);
            if (msgEntityVo != null && ResultCode.Success.equals(msgEntityVo.getCode())) {
                //鉴权通过
                log.info(fileAuth.getAuthType() + "鉴权通过");
            } else {
                assert msgEntityVo != null;
                throw new FrameworkRuntimeException(ResultCode.Fail, msgEntityVo.getMsg());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("云平台鉴权出错：" + fileAuth.getAuthType());
            throw new FrameworkRuntimeException(ResultCode.Fail, e.getMessage());
        }

    }

    @Override
    public List<API> authAndCheckApi(FileAuth fileAuth) throws FrameworkRuntimeException {
        String url = cloudServerUrl + "tool/fileAuth";
        MsgEntityVo msgEntityVo;
        try {
            //设置请求头信息
            HttpPost httpPost = new HttpPost(url);
            msgEntityVo = HttpClientUtils.post(httpPost, JSONUtils.toJSONString(fileAuth), 2000);
            if (msgEntityVo != null && ResultCode.Success.equals(msgEntityVo.getCode())) {
                //鉴权通过
                log.info(fileAuth.getAuthType() + "鉴权通过");
                if (msgEntityVo.getData() != null) {
                    return JSONUtils.parseArray(msgEntityVo.getData().toString(),API.class);
                }else {
                    return null;
                }
            } else {
                assert msgEntityVo != null;
                throw new FrameworkRuntimeException(ResultCode.Fail, msgEntityVo.getMsg());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("云平台鉴权出错：" + fileAuth.getAuthType());
            throw new FrameworkRuntimeException(ResultCode.Fail, e.getMessage());
        }
    }

    @Override
    public Boolean checkIp(String ip) {
        String url = cloudServerUrl + "tool/checkIp";
        MsgEntityVo msgEntityVo;
        TestEngineForm form = new TestEngineForm();
        form.setIp(ip);
        try {
            //设置请求头信息
            HttpPost httpPost = new HttpPost(url);
            msgEntityVo = HttpClientUtils.post(httpPost, JSONUtils.toJSONString(form), 2000);
            if (msgEntityVo != null && ResultCode.Success.equals(msgEntityVo.getCode())) {
                //IP通过
                return true;
            } else {
                log.info(ip + " : 不属于测试引擎的IP不能上传/下载");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("IP判断出错");
        }
        return false;
    }

    @Override
    public void vmCosDownAuth(VmCos vmCos) {
        String url = cloudServerUrl + "tool/vmCosDownAuth";
        MsgEntityVo msgEntityVo;
        try {
            //设置请求头信息
            HttpPost httpPost = new HttpPost(url);
            msgEntityVo = HttpClientUtils.post(httpPost, JSONUtils.toJSONString(vmCos), 2000);
            if (msgEntityVo == null) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "不能下载虚拟COS");
            }

            if (!ResultCode.Success.equals(msgEntityVo.getCode())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "不能下载虚拟COS,原因:" + msgEntityVo.getMsg());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("虚拟cos下载鉴权出错");
            throw new FrameworkRuntimeException(ResultCode.Fail, e.getMessage());

        }
    }
}
