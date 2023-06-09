package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestCardForm;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.service.user.UserService;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.form.UserForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController extends Base {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisDao redisDao;

    @GetMapping(value = "/alive", produces = GlobalContext.PRODUCES)
    public String add() {
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/add", produces = GlobalContext.PRODUCES)
    public String add(@RequestBody UserForm userForm) {
        VerificationUtils.string("name", userForm.getName(), false, 64);
        VerificationUtils.string("account", userForm.getAccount(), false, 32);
        VerificationUtils.string("email", userForm.getEmail(), false, 64);
        VerificationUtils.string("roleId", userForm.getRoleId(), false, 36);
        VerificationUtils.string("status", userForm.getStatus());
        VerificationUtils.string("password", userForm.getPassword(), false, 64);
        VerificationUtils.string("phone", userForm.getPhone());
        userService.add(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/edit", produces = GlobalContext.PRODUCES)
    public String edit(@RequestBody UserForm userForm) {
        VerificationUtils.string("id", userForm.getId(), false, 36);
        userService.edit(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/page", produces = GlobalContext.PRODUCES)
    public String page(@RequestBody UserForm userForm) {
        VerificationUtils.integer("page", userForm.getPage());
        VerificationUtils.integer("pageCount", userForm.getPageCount());
        Pagination<UserVo> pagination = userService.page(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, pagination);
    }

    @PostMapping(value = "/login", produces = GlobalContext.PRODUCES)
    public String login(HttpServletRequest request, @RequestBody UserForm userForm) {
        VerificationUtils.string("account", userForm.getAccount(), false, 32);
        VerificationUtils.string("password", userForm.getPassword(), false, 64);
        VerificationUtils.string("uuid", userForm.getUuid(), false, 36);
        VerificationUtils.string("code", userForm.getCode(), false, 6);

        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        userForm.setIp(ip);
        return respString(ResultCode.Success, ResultCode.SUCCESS, userService.login(userForm));
    }

    @PostMapping(value = "/register", produces = GlobalContext.PRODUCES)
    public String register(@RequestBody UserForm userForm) {
        VerificationUtils.string("uuid", userForm.getUuid(), false, 36);
        VerificationUtils.string("name", userForm.getName(), false, 64);
        VerificationUtils.string("account", userForm.getAccount(), false, 32);
        VerificationUtils.string("email", userForm.getEmail(), false, 64);
        VerificationUtils.string("password", userForm.getPassword(), false, 64);
        VerificationUtils.string("phone", userForm.getPhone());
        userService.register(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/forgetPassword", produces = GlobalContext.PRODUCES)
    public String forgetPassword(@RequestBody UserForm userForm) {

        VerificationUtils.string("account", userForm.getAccount(), false, 32);
        VerificationUtils.string("email", userForm.getEmail(), false, 64);
        userService.forgetPassword(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/registerCount", produces = GlobalContext.PRODUCES)
    public String registerCount(@RequestBody UserForm userForm) {
        List<Map<String, Object>> data = userService.userRegisterCount(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }

    @PostMapping(value = "/del", produces = GlobalContext.PRODUCES)
    public String del(@RequestBody UserForm userForm) {
        VerificationUtils.string("id", userForm.getId());
        userService.delete(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/resetPassword", produces = GlobalContext.PRODUCES)
    public String resetPassword(@RequestBody UserForm userForm) {
        VerificationUtils.string("id", userForm.getId());
        VerificationUtils.string("oldPassword", userForm.getOldPassword(), false, 64);
        VerificationUtils.string("password", userForm.getPassword(), false, 64);
        userService.resetPassword(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/editPassword", produces = GlobalContext.PRODUCES)
    public String editPassword(@RequestBody UserForm userForm) {
        VerificationUtils.string("id", userForm.getId());
        VerificationUtils.string("password", userForm.getPassword(), false, 64);
        userService.editPassword(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    /**
     * 获取论坛跳转地址
     * @param userForm
     * @return
     */
    @PostMapping(value = "/forum", produces = GlobalContext.PRODUCES)
    public String forum(@RequestBody UserForm userForm) {
        return respString(ResultCode.Success, ResultCode.SUCCESS, userService.forum());
    }

    @PostMapping(value = "/logout", produces = GlobalContext.PRODUCES)
    public String logout(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        String ticket = request.getHeader("ticket");
        if (StrUtil.isBlank(userId) || StrUtil.isBlank(ticket)) {
            return respString(ResultCode.ParamIllegal, "用户没登陆",null);
        }
        String webTicket = redisDao.getWebUserTicket(userId);

        //登出另ticket失效
        if (ticket.equals(webTicket)) {
            redisDao.delWebUserTicket(userId);
        }
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/setTemplateId", produces = GlobalContext.PRODUCES)
    public String setTemplateId(@RequestBody UserForm userForm) {
        VerificationUtils.string("id", userForm.getId());
        userService.setTemplateId(userForm);
        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
    }

    @PostMapping(value = "/getTemplateId", produces = GlobalContext.PRODUCES)
    public String getTemplateId(@RequestBody UserForm userForm) {
        VerificationUtils.string("id", userForm.getId());
        return respString(ResultCode.Success, ResultCode.SUCCESS, userService.getTemplateId(userForm));
    }
}
