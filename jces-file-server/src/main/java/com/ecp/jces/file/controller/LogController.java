package com.ecp.jces.file.controller;

import com.ecp.jces.code.FileCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.file.service.auth.AuthService;
import com.ecp.jces.file.util.FileUtils;
import com.ecp.jces.file.util.StrUtil;
import com.ecp.jces.global.GlobalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(value = "/log")
public class LogController extends Base {

    @Resource
    private FileUtils fileUtils;
    private static final Logger log = LoggerFactory.getLogger(LogController.class);
    @Resource
    private AuthService authService;

    // 公共测试日志上报
    @RequestMapping(value = "/uploadLog", consumes = "multipart/form-data")
    public String uploadLog(HttpServletRequest request,
                            @RequestParam("file") MultipartFile file) {
        try {
            //IP白名单不鉴权下载
            String ip = request.getHeader("X-Real-IP");
            if (StrUtil.isBlank(ip)) {
                ip = request.getRemoteAddr();
            }
            log.info("上传ip：" + ip);
            //校验是否白名单，白名单才可以下载
            if (!authService.checkIp(ip)) {
                throw new FrameworkRuntimeException(ResultCode.Fail, ip + " : 不属于测试引擎的IP不能上传");
            } else {
                log.info(ip + "通过");
            }

            byte[] bytes = file.getBytes();
            double i = (bytes.length / (1024.0));
            //大于10M,不行
            if (i > 1024 * 10) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "上传文件不能大于10M");
            }

            String suffix = FileUtils.checkFileType(bytes);
            if (suffix == null || !suffix.equals("zip")) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "请上传ZIP类型的日志文件");
            }

            String path = fileUtils.uploadFile(bytes, file.getOriginalFilename(), FileCode.LOG);
            log.info("测试引擎" + ip + "上传了测试日志:" + file.getOriginalFilename() + " path = " + path);
            return respString(ResultCode.Success, ResultCode.SUCCESS,
                    AesUtil2.encryptData(URLEncoder.encode(path, StandardCharsets.UTF_8.name())));
        } catch (Exception e) {
            log.error("上传附件失败", e);
            return respString(ResultCode.Fail, e.getMessage(), null);
        }
    }

    // 业务测试日志上报
    @RequestMapping(value = "/uploadBusinessLog", produces = GlobalContext.PRODUCES)
    public String uploadBusinessLog(HttpServletRequest request,
                                  @RequestParam("file") MultipartFile file) {
        return this.uploadLog(request, file);
    }
}
