package com.ecp.jces.server.controller;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.form.TestMatrixForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.form.extra.ResultForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.service.terminal.TestEngineService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.TestEngineVo;
import com.ecp.jces.vo.extra.JavaCardDataVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 测试引擎调用的接口汇总
 */
@RestController
@RequestMapping("/engine")
public class EngineController extends Base {
    @Autowired
    private TestEngineService engineService;

    private static final Logger logger = LogManager.getFormatterLogger(EngineController.class);

    @PostMapping(value = "/register", produces = GlobalContext.PRODUCES)
    public String register(HttpServletRequest request, @RequestBody TestEngineForm testEngineForm) {
        VerificationUtils.string("id", testEngineForm.getId());
        VerificationUtils.string("name", testEngineForm.getName());
        String ip = request.getHeader("X-Real-IP");
        int port = request.getRemotePort();
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        testEngineForm.setIp(ip);
        testEngineForm.setPort(String.valueOf(port));
        engineService.register(testEngineForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/heartbeat", produces = GlobalContext.PRODUCES)
    public String heartbeat(HttpServletRequest request, @RequestBody TestEngineForm form) {
        VerificationUtils.string("id", form.getId());
        String ip = request.getHeader("X-Real-IP");
        int port = request.getRemotePort();
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        form.setIp(ip);
        form.setPort(String.valueOf(port));
        engineService.heartbeat(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/uploadEngineInfo", produces = GlobalContext.PRODUCES)
    public String uploadEngineInfo(HttpServletRequest request, @RequestBody TestEngineForm form) {
        logger.info("引擎信息上报json:" + JSONUtils.toJSONString(form));
        VerificationUtils.string("id", form.getId());
        VerificationUtils.string("status", form.getStatus());
        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        form.setIp(ip);
        //测试中则置为在线
        if(TestEngineVo.TESTING_STATUS.equals(form.getStatus())){
            form.setStatus(TestEngineVo.ONLINE_STATUS);
        }
        engineService.uploadEngineInfo(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    /**
     * 当测试引擎接收到测试任务时,若是在测试中状态,则调用该接口，把任务变为待测试，等待再一次下发
     * @param form
     * @return
     */
    @PostMapping(value = "/callbackTesting", produces = GlobalContext.PRODUCES)
    public String callbackTesting(HttpServletRequest request, @RequestBody TestTaskForm form) {
        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }

        VerificationUtils.string("id", form.getId());
        engineService.callbackTesting(ip, form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    /**
     * 当测试引擎接收到终止测试任务时,任务终止了，则调用该接口
     * @param form
     * @return
     */
    @PostMapping(value = "/callbackStop", produces = GlobalContext.PRODUCES)
    public String callbackStop(HttpServletRequest request, @RequestBody TestTaskForm form) {
        VerificationUtils.string("id", form.getId());
        VerificationUtils.string("status", form.getStatus());
        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        engineService.callbackStop(ip, form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    /**
     * 虚拟卡测试结果上报
     *
     * @param request
     * @param result
     * @return
     */
    @PostMapping(value = "/vmCosResult", produces = GlobalContext.PRODUCES)
    public String vmCosResult(HttpServletRequest request, @RequestBody JavaCardDataVo result) {
        VerificationUtils.string("testTaskId", result.getTestTaskId());
        VerificationUtils.string("matrixId", result.getMatrixId());
        VerificationUtils.string("matrixStatus", result.getMatrixStatus());

        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }

        System.out.println(JSONUtils.toJSONString(result));
        engineService.vmCosResult(ip, result);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    /**
     * 公共测试结果上报
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/uploadResult", produces = GlobalContext.PRODUCES)
    public String uploadResult(HttpServletRequest request, @RequestBody ResultForm form) {
        logger.info("公共测试结果上报json:" + JSONUtils.toJSONString(form));
        VerificationUtils.string("testTaskId", form.getTestTaskId());
        VerificationUtils.string("matrixId", form.getMatrixId());
        VerificationUtils.string("cardTypeName", form.getCardTypeName(), true, 36);
        VerificationUtils.string("readerName", form.getReaderName(), false, 64);
        VerificationUtils.string("shorterName", form.getShorterName(), true, 12);
        VerificationUtils.string("commonLogPath", form.getCommonLogPath(), true, 1024);
        VerificationUtils.string("testStart", form.getTestStart());
        VerificationUtils.string("testEnd", form.getTestEnd());
        VerificationUtils.string("result", form.getResult());
        VerificationUtils.integer("rate", form.getRate());

        VerificationUtils.integer("rate", form.getRate());
        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        engineService.uploadResult(ip, form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/uploadBusinessResult", produces = GlobalContext.PRODUCES)
    public String uploadBusinessResult(HttpServletRequest request, @RequestBody ResultForm form) {
        logger.info("业务测试结果上报json:" + JSONUtils.toJSONString(form));
        VerificationUtils.string("testTaskId", form.getTestTaskId());
        VerificationUtils.string("matrixId", form.getMatrixId());
        VerificationUtils.string("cardTypeName", form.getCardTypeName(), true, 36);
        VerificationUtils.string("readerName", form.getReaderName(), false, 64);
        VerificationUtils.string("shorterName", form.getShorterName(), true, 12);
        VerificationUtils.string("customizeLogPath", form.getCustomizeLogPath(), true, 1024);
        VerificationUtils.string("testStart", form.getTestStart());
        VerificationUtils.string("testEnd", form.getTestEnd());
        VerificationUtils.string("result", form.getResult());
        VerificationUtils.integer("rate", form.getRate());

        VerificationUtils.integer("rate", form.getRate());
        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        engineService.uploadBusinessResult(ip, form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }
}
