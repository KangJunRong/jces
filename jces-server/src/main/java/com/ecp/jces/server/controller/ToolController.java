package com.ecp.jces.server.controller;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.FileCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.SysConfigForm;
import com.ecp.jces.form.TestEngineForm;
import com.ecp.jces.form.VmCosDownloadForm;
import com.ecp.jces.form.extra.FileAuth;
import com.ecp.jces.form.extra.VmCos;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.mapper.api.ApiForbiddenDefaultMapper;
import com.ecp.jces.server.dc.mapper.api.ApiForbiddenMapper;
import com.ecp.jces.server.dc.mapper.applet.TestBusinessScriptMapper;
import com.ecp.jces.server.dc.mapper.licenceCode.LicenceCodeMapper;
import com.ecp.jces.server.dc.mapper.task.TestReportMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixMapper;
import com.ecp.jces.server.dc.service.cos.VmCosDownloadService;
import com.ecp.jces.server.dc.service.sys.SysConfigService;
import com.ecp.jces.server.dc.service.terminal.TestEngineService;
import com.ecp.jces.server.dc.service.user.UserService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerifyCodeUtil;
import com.ecp.jces.vo.ApiForbiddenVo;
import com.ecp.jces.vo.SysConfigVo;
import com.ecp.jces.vo.ToolVo;
import com.ecp.jces.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/tool")
public class ToolController extends Base {
    private static final Logger log = LoggerFactory.getLogger(ToolController.class);

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TestEngineService testEngineService;
    @Autowired
    private UserService userService;
    @Autowired
    private TestBusinessScriptMapper testBusinessScriptMapper;
    @Autowired
    private TestReportMapper testReportMapper;

    @Autowired
    private LicenceCodeMapper licenceCodeMapper;

    @Autowired
    private VmCosDownloadService vmCosDownloadService;

    @Autowired
    private ApiForbiddenMapper apiForbiddenMapper;

    @Autowired
    private TestMatrixMapper testMatrixMapper;

    @Autowired
    private ApiForbiddenDefaultMapper defaultMapper;

    @PostMapping(value = "/getIDEAndManualPath", produces = GlobalContext.PRODUCES)
    public String getIDEAndManualPath() throws UnsupportedEncodingException {
        SysConfigForm sysConfig = new SysConfigForm();
        sysConfig.setLabel("java_card_IDE_path");
        SysConfigVo idePathConfig = sysConfigService.getByLabel(sysConfig);
        sysConfig.setLabel("java_card_IDE_user_manual_path");
        SysConfigVo ideUserManualPathConfig = sysConfigService.getByLabel(sysConfig);
        if (idePathConfig == null || StringUtils.isBlank(idePathConfig.getValue())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "没有配置java card IDE 下载路径");
        }
        if (ideUserManualPathConfig == null || StringUtils.isBlank(ideUserManualPathConfig.getValue())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "没有配置java card IDE 用户手册下载路径");
        }
        ToolVo toolVo = new ToolVo();
        toolVo.setJavaCardIDEPath(AesUtil2.encryptData(URLEncoder.encode(idePathConfig.getValue(), StandardCharsets.UTF_8.name())));
        toolVo.setJavaCardIDEUserManualPath(AesUtil2.encryptData(URLEncoder.encode(ideUserManualPathConfig.getValue(), StandardCharsets.UTF_8.name())));
        return respString(ResultCode.Success, ResultCode.SUCCESS, toolVo);
    }


    /**
     * 获取图片验证码
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "/getVerifyCodeImg")
    public void getVerifyCodeImg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uuid = request.getParameter("uuid");
        if (StrUtil.isBlank(uuid)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "缺少参数");
        }

        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        // 生成随机字串
        String verifyCode = VerifyCodeUtil.generateVerifyCode(4);
        // 生成图片
        int w = 116, h = 36;
        VerifyCodeUtil.outputImage(w, h, response.getOutputStream(), verifyCode);
        redisDao.setValueTtl(uuid, verifyCode, 60L);
    }

    /**
     * 检查IP是否是测试引擎的
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/checkIp", produces = GlobalContext.PRODUCES)
    public String checkIp(@RequestBody TestEngineForm form) {
        return respString(testEngineService.findByIp(form) != null ? ResultCode.Success : ResultCode.Fail,
                ResultCode.SUCCESS, null);
    }

    /**
     * 文件上传下载鉴权
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/fileAuth", produces = GlobalContext.PRODUCES)
    public String fileAuth(@RequestBody FileAuth form) {
        UserVo userVo = authAccount(form.getAuth());
        //管理员不需要鉴权
        if (ConstantCode.ROLE_ADMINISTRATORS.equals(userVo.getRoleId())) {
            return respString(ResultCode.Success, ResultCode.SUCCESS, null);
        }

        //开发者
        switch (form.getAuthType()) {
            case FileCode.UPLOAD_CAP_OR_ZIP:
                //获取禁用API列表
                if(form.getIsCheckApi()!=null&&form.getIsCheckApi()){

                    //配置了模板就是安全审核员，不需要做禁用API检测
                    if(StrUtil.isBlank(userVo.getTemplateId())){
                        List<ApiForbiddenVo> list = apiForbiddenMapper.findByUserId(userVo.getId());

                        //配置没有则查看默认有没有
                        if(list.size() == 0){
                            return respString(ResultCode.Success, ResultCode.SUCCESS,defaultMapper.list());
                        }
                        return respString(ResultCode.Success, ResultCode.SUCCESS,list);
                    }else{
                        return respString(ResultCode.Success, ResultCode.SUCCESS, null);
                    }
                }
                else{
                    return respString(ResultCode.Success, ResultCode.SUCCESS, null);
                }
            case FileCode.UPLOAD_SCRIPT:
                return respString(ResultCode.Success, ResultCode.SUCCESS, null);
            case FileCode.UPLOAD_COMMON_SCRIPT:
                return respString(ResultCode.Fail, "只有管理员才可以上传公共脚本", null);
            case FileCode.DOWNLOAD_COMMON_SCRIPT:
                return respString(ResultCode.Fail, "只有管理员才可以下载公共脚本", null);
            case FileCode.UPLOAD_FILE:
                return respString(ResultCode.Fail, "只有管理员才可以上传文档", null);
            case FileCode.DOWNLOAD_LOG:
                if (!userVo.getId().equals(testReportMapper.findUserIdByLogPath(form.getPath()))) {
                    return respString(ResultCode.Fail, "没有权限", null);
                } else {
                    return respString(ResultCode.Success, ResultCode.SUCCESS, null);
                }
            case FileCode.DOWNLOAD_BUSINESS:
                System.out.println(form.getPath());
                //test_business_log -> test_schedule -> test_task -> create_user
                if (!userVo.getId().equals(testBusinessScriptMapper.findUserIdByLogPath(form.getPath()))) {
                    return respString(ResultCode.Fail, "没有权限", null);
                } else {
                    return respString(ResultCode.Success, ResultCode.SUCCESS, null);
                }
            default:
                return respString(ResultCode.Fail, "未定义的接口类型", null);
        }
    }

    private UserVo authAccount(String auth) {
        String userId = auth.split("\\|")[0];
        String ticket = auth.split("\\|")[1];
        if (StrUtil.isBlank(userId) || StrUtil.isBlank(ticket)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "用户没登陆");
        }
        String webTicket = redisDao.getWebUserTicket(userId);
        if (webTicket == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "用户没登陆");
        }
        if (!ticket.equals(webTicket)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "ticket错误");
        }
        UserVo user = userService.findById(userId);
        if (user == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "用户不存在");
        }
        if (ResultCode.DEL == user.getDelFlg()) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "账号已被删除");
        }
        if (ConstantCode.USER_STATUS_DISABLED.equals(user.getStatus())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "账号已被停用");
        }
        return user;
    }

    /**
     * 虚拟COS下载鉴权并记录日志
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/vmCosDownAuth", produces = GlobalContext.PRODUCES)
    public String vmCosDownAuth(@RequestBody VmCos form) {
        log.info("虚拟COS下载IP：" + form.getIp());
        if (StrUtil.isNotBlank(form.getMachineCode())) {
            if (licenceCodeMapper.findByCode(form.getMachineCode()) != null) {

                //插入库
                VmCosDownloadForm cosDownloadForm = new VmCosDownloadForm();
                cosDownloadForm.setId(StrUtil.newGuid());
                cosDownloadForm.setCosNo(form.getNo());
                cosDownloadForm.setIp(form.getIp());
                cosDownloadForm.setCosVersion(form.getVersionNo());
                cosDownloadForm.setDownloadType(ConstantCode.COS_DOWNLOAD_IDE);
                cosDownloadForm.setDownloadTime(new Date());
                vmCosDownloadService.add(cosDownloadForm);
                return respString(ResultCode.Success, ResultCode.SUCCESS, null);
            } else {
                throw new FrameworkRuntimeException(ResultCode.Fail, "不存在的机器码");
            }
        }


        if (StrUtil.isNotBlank(form.getMatrixId())) {
            if (testMatrixMapper.findById(form.getMatrixId()) != null) {
                //插入库
                VmCosDownloadForm cosDownloadForm = new VmCosDownloadForm();
                cosDownloadForm.setId(StrUtil.newGuid());
                cosDownloadForm.setCosNo(form.getNo());
                cosDownloadForm.setIp(form.getIp());
                cosDownloadForm.setCosVersion(form.getVersionNo());
                cosDownloadForm.setDownloadType(ConstantCode.COS_DOWNLOAD_ENGINE);
                cosDownloadForm.setDownloadTime(new Date());
                vmCosDownloadService.add(cosDownloadForm);
                return respString(ResultCode.Success, ResultCode.SUCCESS, null);
            }else {
                throw new FrameworkRuntimeException(ResultCode.Fail, "不存在的矩阵ID");
            }
        }
        throw new FrameworkRuntimeException(ResultCode.Fail, "参数缺失");

    }
}
