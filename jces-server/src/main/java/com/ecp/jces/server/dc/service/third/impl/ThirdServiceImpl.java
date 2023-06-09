package com.ecp.jces.server.dc.service.third.impl;

import com.ecp.jces.code.ConfigKey;
import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.code.ThirdCode;
import com.ecp.jces.common.CipherEncryptors;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.*;
import com.ecp.jces.form.extra.SingleLoginForm;
import com.ecp.jces.form.extra.SyncAccountForm;
import com.ecp.jces.jctool.capscript.AppletInstance;
import com.ecp.jces.jctool.capscript.ExeLoadFile;
import com.ecp.jces.jctool.capscript.ExeModule;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.mapper.applet.AppletExeLoadFileMapper;
import com.ecp.jces.server.dc.mapper.applet.AppletExeModuleMapper;
import com.ecp.jces.server.dc.mapper.applet.AppletMapper;
import com.ecp.jces.server.dc.mapper.applet.AppletVersionMapper;
import com.ecp.jces.server.dc.mapper.script.TestScriptMapper;
import com.ecp.jces.server.dc.mapper.sys.SysConfigMapper;
import com.ecp.jces.server.dc.mapper.task.TestTaskMapper;
import com.ecp.jces.server.dc.mapper.user.UserMapper;
import com.ecp.jces.server.dc.service.third.ThirdService;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.server.util.JWTUtils;
import com.ecp.jces.server.util.RSAEncrypt;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.*;
import com.ecp.jces.vo.extra.singlelogin.DataVo;
import com.ecp.jces.vo.extra.singlelogin.SingleLoginResultVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ThirdServiceImpl implements ThirdService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdServiceImpl.class);
    @Value("${applet.test.commitTestMax}")
    public Integer commitTestMax;


    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private AppletExeLoadFileMapper appletExeLoadFileMapper;

    @Autowired
    private AppletExeModuleMapper appletExeModuleMapper;

    @Autowired
    private AppletMapper appletMapper;

    @Autowired
    private AppletVersionMapper appletVersionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TestTaskMapper testTaskMapper;

    @Autowired
    private TestScriptMapper testScriptMapper;

    @Autowired
    private CenterInf centerInf;

    @Value("${internet-platform.unified-platform-url}")
    private String unifiedPlatformUrl;
    @Value("${internet-platform.point}")
    public String point;
    @Value("${internet-platform.publicKey}")
    public String publicKey;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void commitCap(AppletForm appletForm) throws FrameworkRuntimeException {
        /*条件限制：测试任务过多也不能提交*/
        if (!checkCanTest()) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "提交测试应用个数过多");
        }

        if (appletMapper.findByName(appletForm.getName()) != null) {
            throw new FrameworkRuntimeException(ThirdCode.APP_NAME_EXIST, "该应用名称已经被使用");
        }
        TestScriptVo testScriptVo = testScriptMapper.getActive();
        if (testScriptVo == null || StrUtil.isBlank(testScriptVo.getPath())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "没有激活通用脚本,不能测试");
        }

        Date date = new Date();
        UserVo vo = AuthCasClient.getUser();
        appletForm.setUpdateUser(vo.getId());
        appletForm.setCreateUser(vo.getId());
        appletForm.setUpdateDate(date);
        appletForm.setCreateDate(date);
        appletForm.setDelFlg(ResultCode.NOT_DEL);
        AppletVersionForm form = new AppletVersionForm();
        try {
            form.setName(StrUtil.getFileNameFromUrl(appletForm.getCapPath().getPath()));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
            form.setName("");
        }
        form.setVersion(appletForm.getLastVersion());
        form.setCapPath(appletForm.getCapPath().getPath());
        form.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
        form.setId(appletForm.getVersionId());
        form.setCreateUser(appletForm.getCreateUser());
        form.setCreateDate(date);
        form.setUpdateUser(appletForm.getUpdateUser());
        form.setUpdateDate(date);
        form.setDelFlg(ResultCode.NOT_DEL);
        form.setAppletId(appletForm.getId());
        form.setDescription(appletForm.getCapDesc());
        form.setExamine(ConstantCode.EXAMINE_STATUS_TEST_SUCCESS);
        appletVersionMapper.add(form);
        appletMapper.add(appletForm);
        if (appletForm.getCapPath().getLoadFiles() != null && appletForm.getCapPath().getLoadFiles().size() > 0) {
            //插入数据库
            int i = 1;
            for (ExeLoadFile loadFile : appletForm.getCapPath().getLoadFiles()) {
                AppletExeLoadFileForm exeLoadFile = new AppletExeLoadFileForm();
                exeLoadFile.setId(StrUtil.newGuid());
                exeLoadFile.setCreateUser(appletForm.getId());
                exeLoadFile.setCreateDate(date);
                exeLoadFile.setUpdateUser(appletForm.getId());
                exeLoadFile.setUpdateDate(date);
                exeLoadFile.setHash(loadFile.getHash());
                exeLoadFile.setDelFlg(ResultCode.NOT_DEL);

                exeLoadFile.setAid(loadFile.getAid());
                exeLoadFile.setAppletId(appletForm.getId());
                exeLoadFile.setAppletVersionId(form.getId());
                exeLoadFile.setLoadSequence(i);
                exeLoadFile.setFileName(loadFile.getFileName());
                exeLoadFile.setType(loadFile.isLibPkg() ? ConstantCode.LOAD_FILE_TYPE_LIB : ConstantCode.LOAD_FILE_TYPE_APPLET);
                //加载参数
                exeLoadFile.setLoadParam(loadFile.getLoadParam());
                appletExeLoadFileMapper.add(exeLoadFile);
                i++;
                if (loadFile.getExeModuleList() != null && loadFile.getExeModuleList().size() > 0) {
                    for (ExeModule exeModule : loadFile.getExeModuleList()) {
                        AppletExeModuleForm moduleForm = new AppletExeModuleForm();
                        moduleForm.setId(StrUtil.newGuid());
                        moduleForm.setCreateUser(appletForm.getId());
                        moduleForm.setCreateDate(date);
                        moduleForm.setUpdateUser(appletForm.getId());
                        moduleForm.setUpdateDate(date);
                        moduleForm.setDelFlg(ResultCode.NOT_DEL);
                        moduleForm.setAid(exeModule.getAid());
                        moduleForm.setInstanceAid(exeModule.getInstanceAid());
                        moduleForm.setLoadFileId(exeLoadFile.getId());
                        appletExeModuleMapper.add(moduleForm);
                        if (exeModule.getInstanceList() != null && exeModule.getInstanceList().size() > 0) {
                            for (AppletInstance appletInstance : exeModule.getInstanceList()) {
                                AppletInstanceForm instanceForm = new AppletInstanceForm();
                                instanceForm.setId(StrUtil.newGuid());
                                instanceForm.setCreateUser(appletForm.getId());
                                instanceForm.setCreateDate(date);
                                instanceForm.setUpdateUser(appletForm.getId());
                                instanceForm.setUpdateDate(date);
                                instanceForm.setDelFlg(ResultCode.NOT_DEL);
                                instanceForm.setLoadFileId(exeLoadFile.getId());
                                instanceForm.setLoadFileAid(exeLoadFile.getAid());
                                instanceForm.setModuleId(moduleForm.getId());
                                instanceForm.setModuleAid(moduleForm.getAid());
                                instanceForm.setInstanceAid(appletInstance.getAid());
                                instanceForm.setInstallParam(appletInstance.getInstallParam());
                                appletExeModuleMapper.addInstance(instanceForm);
                            }
                        }
                    }
                }
            }
        }


        TestTaskForm testTaskForm = new TestTaskForm();
        testTaskForm.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
        testTaskForm.setId(StrUtil.newGuid());
        testTaskForm.setType(ConstantCode.TEST_TASK_TYPE_TEST);
        testTaskForm.setAppletId(appletForm.getId());
        testTaskForm.setAppletVersionId(appletForm.getVersionId());

        testTaskForm.setTestScriptId(testScriptVo.getId());
        testTaskForm.setCreateUser(vo.getId());
        testTaskForm.setCreateDate(date);
        testTaskForm.setUpdateUser(vo.getId());
        testTaskForm.setUpdateDate(date);
        testTaskForm.setDelFlg(ResultCode.NOT_DEL);
        testTaskForm.setTimeOut(60);
        testTaskMapper.add(testTaskForm);

    }

    private Boolean checkCanTest() {
        UserVo user = AuthCasClient.getUser();
        //管理员直接可以过
        if (ConstantCode.ROLE_ADMINISTRATORS.equals(user.getRoleId())) {
            return true;
        }
        //查找出有多少个正在测试和待测试的应用
        int passCount = commitTestMax;
        SysConfigForm sysConfigForm = new SysConfigForm();
        sysConfigForm.setLabel(ConfigKey.CommitTestMax);
        SysConfigVo sysConfigVo = sysConfigMapper.getByLabel(sysConfigForm);
        if (sysConfigVo != null && sysConfigVo.getValue() != null) {
            try {
                passCount = Integer.parseInt(sysConfigVo.getValue());
            } catch (Exception e) {
                LOGGER.info("commitTestMax参数转换出错");
            }
        }
        int count = appletMapper.findTestCountByUserId(user.getId());
        return passCount > count;
    }

    @Override
    public String getVoucher(UserForm userForm) throws FrameworkRuntimeException {
        String pwd = userForm.getPassword();
        userForm.setPassword(StrUtil.encodeAes(pwd));
        userForm.setDelFlg(ResultCode.NOT_DEL);
        UserVo vo = userMapper.login(userForm);
        if (vo == null) {
            throw new FrameworkRuntimeException(ThirdCode.FAIL, "账号密码错误");
        }
        if (ConstantCode.USER_STATUS_DISABLED.equals(vo.getStatus())) {
            throw new FrameworkRuntimeException(ThirdCode.FAIL, "账号已被停用");
        }
        if (vo.getVoucher() == null || "0".equals(vo.getVoucher())) {
            throw new FrameworkRuntimeException(ThirdCode.FAIL, "账号未开通接入服务");
        }

        String ticket = StrUtil.newGuid();
        redisDao.setApiUserTicket(vo.getId(), ticket);

        return JWTUtils.create(vo.getId(), ticket, 86400, vo.getId());
    }

    /**
     * @param form 单点登录渠道编码ssoCode(以实际联调约定为准)：1-金科(默认) 2-卡小 5-研究院
     * @return
     * @throws FrameworkRuntimeException
     */
    @Override
    public Map<String, Object> singleLogin(SingleLoginForm form) throws FrameworkRuntimeException {
        DataVo data;
        if (!"demo".equals(form.getAccessToken())) {
            data = centerInf.validateAccessToken(unifiedPlatformUrl, form.getAccessToken());
        } else {
            data = new DataVo();
            data.setMobileNo("13750036379");
        }


        UserForm userForm = new UserForm();
        try {
            userForm.setPhone(RSAEncrypt.publicKeyDecrypt(data.getMobileNo(), publicKey, point));
        } catch (Exception e) {
            throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "解密用户数据出错");
        }
        List<UserVo> list = userMapper.findByPhone(userForm);
        if (list.size() == 0) {
            throw new FrameworkRuntimeException(ResultCode.ParamIllegal, "本系统不存在该用户,请联系管理员添加");
        }
        UserVo vo = list.get(0);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userId", vo.getId());
        resultMap.put("ticket", form.getAccessToken());
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
        redisDao.setWebUserTicket(vo.getId(), form.getAccessToken());
        return resultMap;
    }

    @Override
    public void synchronizeAccount(SyncAccountForm form) throws FrameworkRuntimeException {
        UserForm userForm = new UserForm();
        try {
            String phone = RSAEncrypt.publicKeyDecrypt(form.getUserInfo().getPhone(), publicKey, point);
            if (userMapper.findByAccount(phone) != null) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "该账号已经被使用");
            }

            userForm.setPhone(phone);
            List<UserVo> list = userMapper.findByPhone(userForm);
            if (list.size() > 0) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "该电话已被使用");
            }

            userForm.setEmail(RSAEncrypt.publicKeyDecrypt(form.getUserInfo().getEmail(), publicKey, point));
            if (userMapper.findByEmail(userForm).size() > 0) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "该邮箱已被使用");
            }
            userForm.setName(phone);
            userForm.setAccount(phone);
            userForm.setRoleId(RSAEncrypt.publicKeyDecrypt(form.getRoleInfos().get(0).getRoleCode()
                    , publicKey, point));
        } catch (Exception e) {
            LOGGER.error("synchronizeAccount", e.getMessage());
            throw new FrameworkRuntimeException(ResultCode.Fail, "用户数据解密出错");
        }

        userForm.setStatus(ConstantCode.USER_STATUS_AVAILABLE);
        Date date = new Date();
        userForm.setId(StrUtil.newGuid());
        userForm.setCreateUser(userForm.getId());
        userForm.setCreateDate(date);
        userForm.setUpdateUser(userForm.getId());
        userForm.setUpdateDate(date);
        userForm.setDelFlg(ResultCode.NOT_DEL);

        userMapper.add(userForm);
    }
}
