package com.ecp.jces.server.controller;

import com.ecp.jces.auth.AuthJwt;
import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.code.ThirdCode;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.ApiLogForm;
import com.ecp.jces.form.AppletForm;
import com.ecp.jces.form.UserForm;
import com.ecp.jces.form.extra.SingleLoginForm;
import com.ecp.jces.form.extra.SyncAccountForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.jctool.capscript.ExeLoadFile;
import com.ecp.jces.jctool.capscript.exception.ApplicationCheckException;
import com.ecp.jces.jctool.util.CapUtil;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.mapper.sys.SysConfigMapper;
import com.ecp.jces.server.dc.service.api.ApiLogService;
import com.ecp.jces.server.dc.service.applet.AppletService;
import com.ecp.jces.server.dc.service.third.ThirdService;
import com.ecp.jces.server.dc.service.user.UserService;
import com.ecp.jces.server.util.FileUtils;
import com.ecp.jces.server.util.JWTUtils;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import com.ecp.jces.vo.AppletVo;
import com.ecp.jces.vo.UserVo;
import com.ecp.jces.vo.extra.CapUploadVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

@RestController
@RequestMapping("/third")
public class ThirdController extends Base {

    @Autowired
    private FileUtils fileUtils;
    private static final Logger log = LoggerFactory.getLogger(ThirdController.class);
    @Value("${applet.test.commitTestMax}")
    public Integer commitTestMax;

    @Value("${param.third.uploadUrl}")
    private String uploadUrl;

    @Autowired
    private ThirdService thirdService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppletService appletService;

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private ApiLogService apiLogService;

    /**
     * 第三方提交检测应用
     *
     * @param appName 应用名称
     * @param appName 应用参数
     * @param auth    凭证
     * @return
     */
    @RequestMapping(value = "/capCommit", consumes = "multipart/form-data")
    public String capCommit(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(name = "Authorization") String auth,
            @RequestParam(name = "version") String version,
            @RequestParam(name = "appName") String appName) {
        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        Long start = System.currentTimeMillis();

        String value = sysConfigMapper.getValueByLabel(ConstantCode.COS_VERSION_LABEL);

        if(StrUtil.isBlank(value)){
            throw new FrameworkRuntimeException(ThirdCode.FAIL, "请先在系统配置上定义cos版本");
        }

        if(!value.contains(version)){
            throw new FrameworkRuntimeException(ThirdCode.COS_VERSION_NOT_EXIST, "COS版本不存在");
        }


        UserVo userVo = auth(auth);

        Integer v = 1;
        String versionId = StrUtil.newGuid();
        String appId = StrUtil.newGuid();

        AppletForm form = new AppletForm();
        form.setId(appId);
        form.setName(appName);
        form.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
        form.setLastVersion(v);
        form.setVersionId(versionId);
        form.setVersionNo(version);

        ApiLogForm apiLogForm = new ApiLogForm();
        apiLogForm.setId(StrUtil.newGuid());
        apiLogForm.setIp(ip);
        apiLogForm.setName("第三方提交检测应用");
        apiLogForm.setUri(request.getRequestURI());
        apiLogForm.setDelFlg(ResultCode.NOT_DEL);
        apiLogForm.setCreateUser(userVo.getAccount());
        apiLogForm.setUpdateUser(userVo.getAccount());
        apiLogForm.setCreateDate(new Date());
        apiLogForm.setUpdateDate(apiLogForm.getCreateDate());

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                if (!"zip".equals(fileUtils.checkFileType(bytes))) {
                    apiLogForm.setResult(ResultCode.Fail);
                    apiLogForm.setResponseTime(System.currentTimeMillis() - start);
                    apiLogService.add(apiLogForm);
                    throw new FrameworkRuntimeException(ThirdCode.FAIL, "只能上传ZIP压缩包");
                }
                double i = (bytes.length / (1024.0));
                //大于2M,不行
                if (i > 1024 * 2) {
                    apiLogForm.setResult(ResultCode.Fail);
                    apiLogForm.setResponseTime(System.currentTimeMillis() - start);
                    apiLogService.add(apiLogForm);
                    throw new FrameworkRuntimeException(ThirdCode.FAIL, "上传文件不能大于2M");
                }
                String path = fileUtils.uploadFile(bytes, file.getOriginalFilename(), appId + File.separator + v);
                log.info(userVo.getAccount() + "上传了文件:" + file.getOriginalFilename() + " path = " + path);

                LinkedList<ExeLoadFile> loadFiles = null;
                try {
                    loadFiles = CapUtil.analysisAppPackage(path);
                } catch (ApplicationCheckException e) {
                    if (!fileUtils.deleteAllFile(uploadUrl + appId + File.separator)) {
                        System.gc();
                        Thread.sleep(50);
                        fileUtils.deleteAllFile(uploadUrl + appId + File.separator);
                    }
                    throw new FrameworkRuntimeException(ResultCode.Fail, e.getMessage());
                }


                log.info(JSONUtils.toJSONString(loadFiles));

                CapUploadVo capUploadVo = new CapUploadVo();
                capUploadVo.setLoadFiles(loadFiles);
                capUploadVo.setPath(URLEncoder.encode(path, StandardCharsets.UTF_8.name()));

                form.setCapPath(capUploadVo);

                AuthCasClient.add(userVo);
                //提交
                thirdService.commitCap(form);

            } catch (Exception e) {
                log.error("uploadCapOrZip上传附件失败", e);
                apiLogForm.setResult(ResultCode.Fail);
                apiLogForm.setResponseTime(System.currentTimeMillis() - start);
                apiLogService.add(apiLogForm);
                return respString(ThirdCode.FAIL, e.getMessage(), null);
            }
        } else {
            apiLogForm.setResult(ResultCode.Fail);
            apiLogForm.setResponseTime(System.currentTimeMillis() - start);
            apiLogService.add(apiLogForm);
            return respString(ThirdCode.FAIL, "空文件！", null);
        }

        AppletVo vo = new AppletVo();
        vo.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
        vo.setId(appId);
        vo.setName(appName);

        apiLogForm.setResult(ResultCode.Success);
        apiLogForm.setAppletVersionId(versionId);
        apiLogForm.setResponseTime(System.currentTimeMillis() - start);
        apiLogService.add(apiLogForm);

        return respString(ThirdCode.Success, ThirdCode.SUCCESS, vo);
    }

    @PostMapping(value = "/capResult", produces = GlobalContext.PRODUCES)
    public String capResult(
            @RequestHeader(name = "Authorization") String auth,
            @RequestBody AppletForm form) throws IOException {
        VerificationUtils.string("id", form.getId());
        auth(auth);
        Map<String, Object> data = appletService.testDetailApi(form);
        return respString(ResultCode.Success, ResultCode.SUCCESS, data);
    }

    @PostMapping(value = "/login", produces = GlobalContext.PRODUCES)
    public String login(@RequestBody UserForm form) {
        VerificationUtils.string("account", form.getAccount());
        VerificationUtils.string("password", form.getPassword());
        return respString(ResultCode.Success, ResultCode.SUCCESS, thirdService.getVoucher(form));
    }

    private UserVo auth(String auth) {

        AuthJwt authJwt = JWTUtils.verify(auth);
        if (authJwt == null) {
            throw new FrameworkRuntimeException(ThirdCode.AUTH_INVALID, "凭证无效");
        }

        String userId = authJwt.getUserId();
        String voucher = authJwt.getTicket();

        String ticket = redisDao.getApiUserTicket(userId);

        if (ticket == null) {
            throw new FrameworkRuntimeException(ThirdCode.AUTH_INVALID, "凭证无效");
        }
        if (!ticket.equals(voucher)) {
            throw new FrameworkRuntimeException(ThirdCode.AUTH_INVALID, "凭证错误");
        }

        UserVo userVo = userService.findById(userId);
        if (userVo == null) {
            throw new FrameworkRuntimeException(ThirdCode.FAIL, "无此用户");
        }
        if (ConstantCode.USER_STATUS_DISABLED.equals(userVo.getStatus())) {
            throw new FrameworkRuntimeException(ThirdCode.FAIL, "该账号处于禁用状态");
        }
        return userVo;
    }

    /**
     * 单点登录
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/singleLogin", produces = GlobalContext.PRODUCES)
    public String singleLogin(@RequestBody SingleLoginForm form) {
        VerificationUtils.string("ssoCode", form.getSsoCode());
        VerificationUtils.string("accessToken", form.getAccessToken(), false, 128);
        return respString(ResultCode.Success, ResultCode.SUCCESS, thirdService.singleLogin(form));
    }

    /**
     * 账号信息同步
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/synchronizeAccount", produces = GlobalContext.PRODUCES)
    public String synchronizeAccount(@RequestBody SyncAccountForm form) {
        VerificationUtils.string("platformType", form.getPlatformType());
        VerificationUtils.obj("userInfo", form.getUserInfo());
        VerificationUtils.objList("roleInfos", form.getRoleInfos());
        thirdService.synchronizeAccount(form);
        return respString("200", ResultCode.SUCCESS, null);
    }
}
