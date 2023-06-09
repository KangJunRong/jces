package com.ecp.jces.server.third;


import com.alibaba.fastjson.JSONObject;
import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestMatrixForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.form.extra.CardInfoForm;
import com.ecp.jces.form.extra.StartTestForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.mq.MqMsgInfo;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.mapper.applet.TestCheckReportMapper;
import com.ecp.jces.server.dc.mapper.task.TestTaskMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixMapper;
import com.ecp.jces.server.mq.Producer;
import com.ecp.jces.server.util.HttpClientUtils;
import com.ecp.jces.server.util.RSAEncrypt;
import com.ecp.jces.vo.*;
import com.ecp.jces.vo.extra.singlelogin.DataVo;
import com.ecp.jces.vo.extra.singlelogin.SingleLoginResultVo;
import org.apache.http.client.methods.HttpPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @program: project-parent
 * @description:
 * @author: KJR
 * @create: 2020-04-22 10:33
 **/
@Service
public class CenterInf {
    private static final Logger LOGGER = LogManager.getLogger(CenterInf.class);
    @Autowired
    private TestTaskMapper testTaskMapper;
    @Autowired
    private TestCheckReportMapper testCheckReportMapper;
    @Autowired
    private TestMatrixMapper matrixMapper;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private Producer producer;

    public MsgEntityVo taskStart(String testType, String engineId, StartTestForm param) {
        MsgEntityVo result = new MsgEntityVo();
        if (param == null) {
            return result;
        }
        LOGGER.info(JSONUtils.toJSONString(param));
        try {
            MqMsgInfo info = new MqMsgInfo();
            info.setEngineId(engineId);
            if (ConstantCode.TEST_CONTENT_BUSINESS_MSG.equals(testType)) {
                info.setMsgType("physicalCardBusinessTest");
            } else {
                info.setMsgType("physicalCardApplcationTest");
            }
            info.setData(param);
            producer.syncSend(info);
            result.setCode(ResultCode.Success);
        } catch (Exception ex) {
            LOGGER.error("服务器异常", ex);
            result.setCode(ResultCode.Fail);
            result.setMsg("测试机异常," + ex.getMessage());
            return result;
        }
        return result;
    }

    @Deprecated
    public List<CardInfoForm> getReaderCards(TestEngineVo vo) {
        //设置请求头信息
        String url = "http://" + vo.getIp() + ":" + vo.getPort() + "/jces-engine/operate/getReaderCards";
        HttpPost httpPost = new HttpPost(url);
        MsgEntityVo result;
        try {
            JSONObject jsonContent = new JSONObject();
            jsonContent.put("userId", AuthCasClient.getUserId());
            jsonContent.put("method", "getReaderCards");
            result = HttpClientUtils.post(httpPost, jsonContent.toJSONString(), 10000);
        } catch (Exception ex) {
            LOGGER.error("服务器异常", ex);
            throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "测试引擎异常," + ex.getMessage());
        }
        if (result != null) {
            if (ResultCode.Success.equals(result.getCode())) {
                if (result.getData() != null) {
                    String resultStr = result.getData().toString();
                    LOGGER.info("测试引擎读卡器列表:" + resultStr);
                    return JSONUtils.parseArray(resultStr, CardInfoForm.class);
                } else {
                    return null;
                }
            }
            throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "获取测试引擎读卡器列表失败，" + result.getMsg());
        } else {
            throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "获取测试引擎读卡器列表失败，没有返回结果");
        }
    }

    public void getTestMatrixInformation(TestEngineVo vo) {
        try {
            MqMsgInfo info = new MqMsgInfo();
            info.setEngineId(vo.getId());
            info.setMsgType("getTestMatrixInformation");
            info.setData(null);
            producer.syncSend(info);
            LOGGER.info("下发【getTestMatrixInformation】成功");
        } catch (Exception ex) {
            LOGGER.error("服务器异常", ex);
            throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "测试引擎异常," + ex.getMessage());
        }
    }

    /**
     * @param ip
     * @param port
     * @param param
     * @return true表示可以进行实卡下发 false不可以
     */
    public boolean taskJavaCard(String engineId, String ip, String port, StartTestForm param) {
        TestTaskVo testTaskVo = testTaskMapper.findById(param.getTestTaskId());
        TestCheckReportVo testCheckReportVo = testCheckReportMapper.findByAppletIdAndVersionId(testTaskVo.getAppletId(), testTaskVo.getAppletVersionId());
        //没有表示没下发或者没有结果上报,有结果表示已经完成虚拟cos测试
        if (testCheckReportVo != null) {
            return true;
        }
        //查看标记看是否已经下发，已经下发则不进行下发
        String flag = redisDao.getValue(ConstantCode.VM_COS_TEST_FLAG + param.getTestTaskId());

        //null表示没有下发
        if (flag == null) {
            LOGGER.info("下发虚拟COS参数:" + JSONUtils.toJSONString(param));
            try {
                MqMsgInfo info = new MqMsgInfo();
                info.setEngineId(engineId);
                info.setMsgType("virtualCardTest");
                info.setData(param);
                producer.syncSend(info);
            } catch (Exception ex) {
                LOGGER.error("服务器异常", ex);
                return false;
            }
            //下发成功，矩阵状态变为待测试
            TestMatrixForm form = new TestMatrixForm();
            form.setMatrixId(param.getMatrixId());
            form.setMatrixStatus(TestMatrixVo.WAITING_STATUS);
            matrixMapper.update(form);

            //虚拟卡测试也要填入所属矩阵
            TestTaskForm testTaskForm  =new TestTaskForm();
            testTaskForm.setId(testTaskVo.getId());
            testTaskForm.setMatrixId(param.getMatrixId());
            testTaskForm.setTestStart(new Date());
            testTaskMapper.edit(testTaskForm);

            redisDao.setValueTtl(ConstantCode.VM_COS_TEST_FLAG + param.getTestTaskId(), "0", 60L);
        }
        //已经下发则进行实卡测试
        return false;

    }

    public TestMatrixVo getMatrixInfo(TestEngineVo vo, String matrixId) {
        //设置请求头信息
        String url = "http://" + vo.getIp() + ":" + vo.getPort() + "/SmartCardTestEngine/Matrix/getSingleMatrixInformation";
        JSONObject jsonContent = new JSONObject();
        jsonContent.put("matrixId", matrixId);
        String result;
        try {
            HttpPost httpPost = new HttpPost(url);
            result = HttpClientUtils.postSimple(httpPost, jsonContent.toJSONString(), 10000);
        } catch (Exception ex) {
            LOGGER.error("服务器异常", ex);
            return null;
        }
        if (result != null) {
            return JSONObject.parseObject(result, TestMatrixVo.class);
        } else {
            throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "更新测试矩阵失败，没有返回结果");
        }
    }

    public boolean terminateTest(TestEngineVo vo, String matrixId, String testTaskId) {
        JSONObject jsonContent = new JSONObject();
        jsonContent.put("matrixId", matrixId);
        jsonContent.put("testTaskId", testTaskId);
        MqMsgInfo info = new MqMsgInfo();
        info.setEngineId(vo.getId());
        info.setMsgType("stopTestMatrixTest");
        info.setData(jsonContent);
        producer.syncSend(info);
        return true;
    }

    public DataVo validateAccessToken(String url, String accessToken) {
        //设置请求头信息
        JSONObject jsonContent = new JSONObject();
        jsonContent.put("accessToken", accessToken);
        String result;
        try {
            HttpPost httpPost = new HttpPost(url);
            result = HttpClientUtils.postSimple(httpPost, jsonContent.toJSONString(), 10000);
        } catch (Exception ex) {
            throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "单点登录验证失败:" + ex.getMessage());
        }
        if (result != null) {
            LOGGER.info("validateAccessToken返回信息：" + result);
            SingleLoginResultVo res = JSONObject.parseObject(result, SingleLoginResultVo.class);
            if ("200".equals(res.getCode()) && res.getData() != null) {
                return res.getData();
            } else {
                throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "单点登录验证失败:" + res.getMsg());
            }
        } else {
            throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "单点登录验证失败");
        }
    }
}
