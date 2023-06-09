package com.ecp.jces.server.dc.service.user.impl;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.common.CipherEncryptors;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.SysConfigForm;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.mapper.role.RoleMapper;
import com.ecp.jces.server.dc.mapper.user.UserMapper;
import com.ecp.jces.server.dc.service.sys.SysConfigService;
import com.ecp.jces.server.dc.service.user.UserService;
import com.ecp.jces.server.util.MailUtil;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.UserForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.vo.RoleVo;
import com.ecp.jces.vo.SysConfigVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.UrlBase64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private RoleMapper roleDao;
    @Autowired
    private SysConfigService sysConfigService;

    @Value("${forum.url.admin}")
    private String adminUrl;
    @Value("${forum.url.developer}")
    private String developerUrl;

    @Override
    public UserVo findById(String userId) throws FrameworkRuntimeException {
        return userMapper.findById(userId);
    }

    @Override
    public List<UserVo> list(UserForm userForm) throws FrameworkRuntimeException {

        userForm.setDelFlg(ResultCode.NOT_DEL);
        return userMapper.list(userForm);
    }

    @Override
    public Pagination<UserVo> page(UserForm form) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(form.getPage(), form.getPageCount());
        List<UserVo> list = userMapper.list(form);
        Pagination<UserVo> pagination = new Pagination<>(form.getPage(), form.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public void add(UserForm userForm) throws FrameworkRuntimeException {

        if (userMapper.findByAccount(userForm.getAccount()) != null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该账号已经被使用");
        }
        List<UserVo> list = userMapper.findByPhone(userForm);
        if (list.size() > 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该电话已被使用");
        }

        UserForm form = new UserForm();
        form.setEmail(userForm.getEmail());
        if (userMapper.findByEmail(form).size() > 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该邮箱已被使用");
        }

        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        userForm.setId(StrUtil.newGuid());
        userForm.setCreateUser(vo.getId());
        userForm.setCreateDate(date);
        userForm.setUpdateUser(vo.getId());
        userForm.setUpdateDate(date);
        userForm.setDelFlg(ResultCode.NOT_DEL);
        userForm.setForumPassword(CipherEncryptors.encrypt(userForm.getPassword()));
        userForm.setPassword(StrUtil.encodeAes(userForm.getPassword()));
        userMapper.add(userForm);
    }

    @Override
    public void edit(UserForm userForm) throws FrameworkRuntimeException {
        UserVo vo = userMapper.findById(userForm.getId());
        if (vo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该账号不存在");
        }

        // 检查电话是否被使用
        List<UserVo> list = userMapper.findByPhone(userForm);
        if (list.size() > 1) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该电话已被使用");
        }
        if (list.size() == 1) {
            if (!list.get(0).getId().equals(userForm.getId())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "该电话已被使用");
            }
        }

        // 检查邮箱是否被使用
        list = userMapper.findByEmail(userForm);
        if (list.size() > 1) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该邮箱已被使用");
        }
        if (list.size() == 1) {
            if (!list.get(0).getId().equals(userForm.getId())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "该邮箱已被使用");
            }
        }

        UserVo user = AuthCasClient.getUser();
        Date date = new Date();
        userForm.setUpdateUser(user.getId());
        userForm.setUpdateDate(date);
        userMapper.edit(userForm);
    }

    @Override
    public Map<String, Object> login(UserForm userForm) throws FrameworkRuntimeException {
        String code = redisDao.getValue(userForm.getUuid());
        if (code == null) {
            redisDao.delValue(userForm.getUuid());
            throw new FrameworkRuntimeException(ResultCode.CodeInvalid, "验证码失效");
        }
        if (!userForm.getCode().equals(code)) {
            redisDao.delValue(userForm.getUuid());
            throw new FrameworkRuntimeException(ResultCode.CodeInvalid, "验证码错误");
        }
        String pwd = userForm.getPassword();
        pwd = AesUtil2.decryptAES2(pwd);
        if (StrUtil.isBlank(pwd)) {
            throw new FrameworkRuntimeException(ResultCode.CodeInvalid, "密码错误");
        }

        String vCode = pwd.substring(pwd.length() - 4);

        pwd = pwd.substring(0, pwd.length() - 4);

        if (!code.equals(vCode)) {
            redisDao.delValue(userForm.getUuid());
            throw new FrameworkRuntimeException(ResultCode.CodeInvalid, "密码被篡改");
        }

        userForm.setDelFlg(ResultCode.DEL);
        userForm.setPassword(StrUtil.encodeAes(pwd));
        UserVo vo = userMapper.login(userForm);
        if (vo != null) {
            redisDao.delValue(userForm.getUuid());
            throw new FrameworkRuntimeException(ResultCode.Fail, "账号已被删除");
        }
        userForm.setDelFlg(ResultCode.NOT_DEL);
        vo = userMapper.login(userForm);
        if (vo == null) {
            redisDao.delValue(userForm.getUuid());
            throw new FrameworkRuntimeException(ResultCode.Fail, "账号密码错误");
        }
        if (ConstantCode.USER_STATUS_DISABLED.equals(vo.getStatus())) {
            redisDao.delValue(userForm.getUuid());
            throw new FrameworkRuntimeException(ResultCode.Fail, "账号已被停用");
        }

        // 更新ticket，不能同时登陆
        String ticket = StrUtil.newGuid();
        /*String ticket = redisDao.getWebUserTicket(vo.getId());
        if (ticket == null) {
            ticket = StrUtil.newGuid();
        }*/
        redisDao.setWebUserTicket(vo.getId(), ticket);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userId", vo.getId());
        resultMap.put("ticket", ticket);
        resultMap.put("account", vo.getAccount());
        if (StringUtils.isNotEmpty(vo.getName())) {
            resultMap.put("userName", vo.getName());
        } else {
            resultMap.put("userName", vo.getAccount());
        }

        resultMap.put("status", vo.getStatus());
        resultMap.put("email", vo.getEmail());
        resultMap.put("roleId", vo.getRoleId());
        resultMap.put("templateId", vo.getTemplateId());
        redisDao.delValue(userForm.getUuid());

        if (StrUtil.isBlank(vo.getForumPassword())) {
            UserForm user = new UserForm();
            user.setId(vo.getId());
            user.setForumPassword(CipherEncryptors.encrypt(pwd));
            userMapper.edit(user);
        }
        redisDao.delIpCount(userForm.getIp());
        return resultMap;
    }

    @Override
    public void register(UserForm userForm) throws FrameworkRuntimeException {
        String code = redisDao.getValue(userForm.getUuid());
        if (code == null) {
            redisDao.delValue(userForm.getUuid());
            throw new FrameworkRuntimeException(ResultCode.CodeInvalid, "验证码失效");
        }
        if (!userForm.getCode().equals(code)) {
            redisDao.delValue(userForm.getUuid());
            throw new FrameworkRuntimeException(ResultCode.CodeInvalid, "验证码错误");
        }
        // 检查电话是否被使用
        List<UserVo> list = userMapper.findByPhone(userForm);
        if (list.size() > 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该电话已被使用");
        }

        String pwd = userForm.getPassword();
        pwd = AesUtil2.decryptAES2(pwd);
        if (StrUtil.isBlank(pwd)) {
            throw new FrameworkRuntimeException(ResultCode.CodeInvalid, "密码错误");
        }

        String vCode = pwd.substring(pwd.length() - 4);

        pwd = pwd.substring(0, pwd.length() - 4);

        if (!code.equals(vCode)) {
            redisDao.delValue(userForm.getUuid());
            throw new FrameworkRuntimeException(ResultCode.CodeInvalid, "密码被篡改");
        }

        if (userMapper.findByEmail(userForm).size() > 0) {
            throw new FrameworkRuntimeException(ResultCode.CodeInvalid, "邮箱已被使用");
        }

        RoleVo developerRole = roleDao.getDeveloperRole();
        if (developerRole == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "开发者注册失败！");
        }
        userForm.setRoleId(developerRole.getId());
        userForm.setStatus(ConstantCode.USER_STATUS_AVAILABLE);
        //添加用户到数据库中
        if (userMapper.findByAccount(userForm.getAccount()) != null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该账号已经被使用");
        }
        //设置授权码限额
        SysConfigForm sysConfigForm = new SysConfigForm();
        sysConfigForm.setLabel("licence_code_quota");
        SysConfigVo sysConfigVo = sysConfigService.getByLabel(sysConfigForm);
        Integer licenceCodeQuota = 5; //给个默认值5
        if (sysConfigVo != null && StringUtils.isNotBlank(sysConfigVo.getValue())) {
            licenceCodeQuota = Integer.valueOf(sysConfigVo.getValue());
        }
        userForm.setLicenceCodeQuota(licenceCodeQuota);

        Date date = new Date();
        userForm.setId(StrUtil.newGuid());
        userForm.setCreateUser(userForm.getId());
        userForm.setCreateDate(date);
        userForm.setUpdateUser(userForm.getId());
        userForm.setUpdateDate(date);
        userForm.setDelFlg(ResultCode.NOT_DEL);

        userForm.setForumPassword(CipherEncryptors.encrypt(pwd));
        userForm.setPassword(StrUtil.encodeAes(pwd));
        userMapper.add(userForm);
    }

    @Override
    public void forgetPassword(UserForm userForm) throws FrameworkRuntimeException {
        UserVo user = userMapper.findByAccountAndEmail(userForm);
        if (user == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "账号或者邮箱错误！");
        }
        //生成随机密码
        String newPassword = StrUtil.GetRandomNumberString(6);
        //更新密码到数据库
        UserForm newUserForm = new UserForm();
        newUserForm.setId(user.getId());
        newUserForm.setForumPassword(CipherEncryptors.encrypt(newPassword));
        newUserForm.setPassword(StrUtil.encodeAes(newPassword));
        userMapper.edit(newUserForm);
        //发邮件
        try {
            MailUtil.sendMail(user.getEmail(), "JCES云平台找回密码", "您的登陆密码:" + newPassword);
        } catch (Exception e) {
            LOGGER.error("发送邮件失败", e);

            //把原密码改回来
            newUserForm.setForumPassword(user.getForumPassword());
            newUserForm.setPassword(user.getPassword());
            userMapper.edit(newUserForm);
            throw new FrameworkRuntimeException(ResultCode.Fail, "发送邮件失败!");
        }

    }

    @Override
    public List<Map<String, Object>> userRegisterCount(UserForm userForm) throws FrameworkRuntimeException {
        return userMapper.userCreateCount(userForm);
    }

    @Override
    public void delete(UserForm userForm) throws FrameworkRuntimeException {
        if (ConstantCode.SYSTEM_ADMIN_ID.equals(userForm.getId())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "admin管理员不能被删除!");
        }
        userForm.setDelFlg(ResultCode.DEL);
        userMapper.delete(userForm);
    }

    @Override
    public void resetPassword(UserForm userForm) throws FrameworkRuntimeException {
        userForm.setOldPassword(StrUtil.encodeAes(userForm.getOldPassword()));
        if (StringUtils.isNotBlank(userForm.getOldPassword())) {
            if (!userMapper.isOldPasswordExist(userForm)) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "旧密码不正确!");
            }
        }
        userForm.setForumPassword(CipherEncryptors.encrypt(userForm.getPassword()));
        userForm.setPassword(StrUtil.encodeAes(userForm.getPassword()));
        this.edit(userForm);
    }

    @Override
    public void editPassword(UserForm userForm) throws FrameworkRuntimeException {
        userForm.setForumPassword(CipherEncryptors.encrypt(userForm.getPassword()));
        userForm.setPassword(StrUtil.encodeAes(userForm.getPassword()));
        this.edit(userForm);
    }

    @Override
    public String forum() {

        String url = "?ticket=%s";
        UserVo vo = AuthCasClient.getUser();
        Map<String, String> ticket = new HashMap<>(3);
        ticket.put("userName", vo.getAccount());
        ticket.put("password", CipherEncryptors.decrypt(vo.getForumPassword()));
        if (ConstantCode.ROLE_ADMINISTRATORS.equals(vo.getRoleId())) {
            url = adminUrl + url;
            url = String.format(url, CipherEncryptors.encrypt(JSONUtils.toJSONString(ticket)));

        }

        if (ConstantCode.ROLE_DEVELOPER.equals(vo.getRoleId())) {
            ticket.put("jumpUrl", "aW5kZXg");
            url = developerUrl + url;
            url = String.format(url, CipherEncryptors.encrypt(JSONUtils.toJSONString(ticket)));
        }

        return url;

    }

    @Override
    public void setTemplateId(UserForm userForm) throws FrameworkRuntimeException {
        userMapper.setTemplateId(userForm);
    }

    @Override
    public String getTemplateId(UserForm userForm) throws FrameworkRuntimeException {
        return userMapper.getTemplateId(userForm);
    }

}
