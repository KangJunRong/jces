package com.ecp.jces.server.controller;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.form.TestEngineReaderForm;
import com.ecp.jces.form.extra.ExecutionForm;
import com.ecp.jces.form.extra.ResultForm;
import com.ecp.jces.form.extra.StartTestForm;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.service.applet.TestTaskService;
import com.ecp.jces.server.dc.service.user.UserService;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.form.UserForm;
import com.ecp.jces.global.GlobalContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/whitelist")
public class WhiteListController extends Base {

    @Autowired
    private RedisDao redisDao;


    @PostMapping(value = "/set", produces = GlobalContext.PRODUCES)
    public String set(HttpServletRequest request, @RequestBody TestEngineForm form) {
        VerificationUtils.string("ip白名单", form.getIp(), false, 1024);
        redisDao.setValue(ConstantCode.WHITELIST, form.getIp());
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/get", produces = GlobalContext.PRODUCES)
    public String get(HttpServletRequest request, @RequestBody TestEngineForm form) {
        return respString(ResultCode.Success, ResultCode.SUCCESS, redisDao.getValue(ConstantCode.WHITELIST));
    }
}
