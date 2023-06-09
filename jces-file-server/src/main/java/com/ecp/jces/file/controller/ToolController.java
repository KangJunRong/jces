package com.ecp.jces.file.controller;

import com.eastcompeace.capAnalysis.CapAnalysisUtil;
import com.eastcompeace.capAnalysis.doman.API;
import com.eastcompeace.capAnalysis.doman.AnalysResult;
import com.ecp.jces.code.FileCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.file.service.auth.AuthService;
import com.ecp.jces.file.util.FileUtils;
import com.ecp.jces.file.util.StrUtil;
import com.ecp.jces.form.extra.FileAuth;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.jctool.capscript.ExeLoadFile;
import com.ecp.jces.jctool.capscript.exception.ApplicationCheckException;
import com.ecp.jces.jctool.util.CapUtil;
import com.ecp.jces.vo.extra.CapUploadVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * 文件管理
 */
@RestController
@RequestMapping("/tool")
public class ToolController extends Base {

    private static final Logger log = LoggerFactory.getLogger(ToolController.class);

    @Value("${param.jces.expFilePath}")
    private String expFilePath;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private AuthService authService;

    @Value("${param.jces.uploadUrl}")
    private String uploadUrl;

    private static final String DISPOSITION = "Content-Disposition";

    private static final String FILENAME = "filename=";

    public static final String OCTET = "application/octet-stream; charset=UTF-8";

    /**
     * 内部上传接口,不鉴权
     *
     * @param request
     * @param childPath 路径
     * @return
     */
    @RequestMapping(value = "/uploadFile", consumes = "multipart/form-data")
    public String uploadFile(HttpServletRequest request,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam(name = "childPath") String childPath) {
        try {
            //IP白名单不鉴权下载
            String ip = request.getHeader("X-Real-IP");
            if (StrUtil.isBlank(ip)) {
                ip = request.getRemoteAddr();
            }
            log.info("上传ip：" + ip);
            log.info("上传路径：" + childPath);
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

            String newFilename = fileUtils.uploadFile(bytes, file.getOriginalFilename(), childPath);
            log.info("userId" + "上传了文件:" + newFilename);
            return respString(ResultCode.Success, ResultCode.SUCCESS,
                    URLEncoder.encode(newFilename, StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            log.error("上传附件失败", e);
            return respString(ResultCode.Fail, e.getMessage(), null);
        }
    }

    /**
     * 内部下载接口,不鉴权
     *
     * @param response 响应
     * @param childPath 加密的文件路径
     * @throws IOException
     */
    @RequestMapping(value = "/downloadFile", produces = GlobalContext.PRODUCES)
    public void downloadFile(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "childPath") String childPath) throws IOException {

        String filePath = URLDecoder.decode(childPath, StandardCharsets.UTF_8.name());
        if (!filePath.startsWith(uploadUrl)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "错误的路径");
        }
        //IP白名单不鉴权下载
        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        log.info("下载ip：" + ip);
        log.info("下载路径：" + filePath);
        //校验是否白名单，白名单才可以下载
        if (!authService.checkIp(ip)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, ip + " : 不属于测试引擎的IP不能下载");
        } else {
            log.info(ip + "通过");
        }

        //文件不存在返回
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "文件不存在");
        }
        String fileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name());
        response.setContentType(OCTET);
        response.setHeader(DISPOSITION, FILENAME + fileName);
        responseFile(response, file);
    }


    /**
     * 上传CAP包或者ZIP
     *
     * @param request 请求
     * @param childPath 路径
     * @param auth      验证
     * @return 上传的cap对象
     */
    @RequestMapping(value = "/uploadCapOrZip", produces = GlobalContext.PRODUCES)
    public String uploadCapOrZip(HttpServletRequest request,
                                 @RequestParam(name = "auth") String auth,
                                 @RequestParam(name = "childPath") String childPath) {

        auth = AesUtil2.decryptAES2(auth);
        childPath = AesUtil2.decryptAES2(childPath);
        if (StrUtil.isBlank(auth) || StrUtil.isBlank(childPath)) {
            return respString(ResultCode.ParamIllegal, "参数出错", null);
        }
        if (!childPath.startsWith(FileCode.CAP)) {
            return respString(ResultCode.ParamIllegal, "上传路径不匹配", null);
        }

        log.info(auth);
        log.info(childPath);
        String uploadPath = uploadUrl + childPath + File.separator;
        // 如果目录存在不能上传
        File uploadDir = new File(uploadPath);
        if (uploadDir.isDirectory()) {
            mustDeleteFile(uploadPath);
            return respString(ResultCode.ParamIllegal, "不能重复上传", null);
        }
        FileAuth fileAuth = new FileAuth();
        fileAuth.setAuth(auth);
        fileAuth.setAuthType(FileCode.UPLOAD_CAP_OR_ZIP);
        fileAuth.setPath(uploadPath);
        fileAuth.setIsCheckApi(true);

        List<API> apis = authService.authAndCheckApi(fileAuth);

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (1 != files.size()) {
            return respString(ResultCode.ParamIllegal, "没有找到上传文件", null);
        }
        MultipartFile file;
        for (MultipartFile file1 : files) {
            file = file1;
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();

                    if (!"zip".equals(FileUtils.checkFileType(bytes))) {
                        throw new FrameworkRuntimeException(ResultCode.Fail, "只能上传ZIP压缩包");
                    }

                    double i = (bytes.length / (1024.0));
                    //大于10M,不行
                    if (i > 1024 * 2) {
                        throw new FrameworkRuntimeException(ResultCode.Fail, "上传文件不能大于2M");
                    }

                    String newFilename = fileUtils.uploadFile(bytes, file.getOriginalFilename(), childPath);
                    log.info("userId" + "上传了文件:" + newFilename);

                    LinkedList<ExeLoadFile> loadFiles;

                    try {
                        loadFiles= CapUtil.analysisAppPackage(newFilename);
                    }catch (Exception e){
                        mustDeleteFile(uploadPath);
                        throw new FrameworkRuntimeException(ResultCode.Fail, e.getMessage());
                    }

                    log.info(JSONUtils.toJSONString(loadFiles));

                    //文件禁用api判断
                    if (apis != null && apis.size() > 0) {
                        String capfilePath = uploadUrl + childPath + File.separator + "jcesCapTemp";
                        Long start = System.currentTimeMillis();
                        AnalysResult result = CapAnalysisUtil.checkCap(capfilePath, expFilePath, apis);
                        log.info("检测耗时：" + (System.currentTimeMillis() - start));
                        if (0 == result.getResult()) {
                            if (result.getDisabledApis() != null && result.getDisabledApis().size() > 0) {
                                mustDeleteFile(uploadPath);
                                return respString(ResultCode.ApiFail, "cap检测出禁用API", result.getDisabledApisMethodStrs());
                            }
                        }else {
                            mustDeleteFile(uploadPath);
                            return respString(ResultCode.Fail, "cap检测禁用API出错", null);
                        }
                    }

                    CapUploadVo capUploadVo = new CapUploadVo();
                    capUploadVo.setLoadFiles(loadFiles);

                    capUploadVo.setPath(AesUtil2.encryptData(URLEncoder.encode(newFilename, StandardCharsets.UTF_8.name())));

                    return respString(ResultCode.Success, ResultCode.SUCCESS, capUploadVo);
                } catch (Exception e) {
                    log.error("uploadCapOrZip上传附件失败", e);
                    return respString(ResultCode.Fail, e.getMessage(), null);
                }
            } else {
                return respString(ResultCode.Fail, "空文件！", null);
            }
        }
        return respString(ResultCode.Fail, "上传附件出现了未知的错误", null);
    }

    private void mustDeleteFile(String path){
        if(!FileUtils.deleteAllFile(path)){
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            FileUtils.deleteAllFile(path);
        }
    }

    /**
     * 业务脚本上传
     *
     * @param childPath 路径
     * @param auth      验证
     * @return
     */
    @RequestMapping(value = "/uploadScript", consumes = "multipart/form-data")
    public String uploadScript(@RequestParam("file") MultipartFile file,
                               @RequestParam(name = "auth") String auth,
                               @RequestParam(name = "childPath") String childPath) {


        auth = AesUtil2.decryptAES2(auth);
        childPath = AesUtil2.decryptAES2(childPath);
        if (StrUtil.isBlank(auth) || StrUtil.isBlank(childPath)) {
            return respString(ResultCode.ParamIllegal, "参数出错", null);
        }
        if (!childPath.startsWith(FileCode.CUSTOMIZE_SCRIPT)) {
            return respString(ResultCode.ParamIllegal, "上传路径不匹配", null);
        }

        log.info(auth);
        log.info(childPath);
        String uploadPath = uploadUrl + childPath + File.separator;
        // 如果目录存在不能上传
        File uploadDir = new File(uploadPath);
        if (uploadDir.isDirectory()) {
            return respString(ResultCode.ParamIllegal, "不能重复上传", null);
        }
        FileAuth fileAuth = new FileAuth();
        fileAuth.setAuth(auth);
        fileAuth.setAuthType(FileCode.UPLOAD_SCRIPT);
        fileAuth.setPath(uploadPath);
        authService.auth(fileAuth);

        try {
            byte[] bytes = file.getBytes();
            if (!"zip".equals(FileUtils.checkFileType(bytes))) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "只能上传ZIP压缩包");
            }
            double i = (bytes.length / (1024.0));
            //大于10M,不行
            if (i > 1024 * 10) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "上传文件不能大于10M");
            }

            String newFilename = fileUtils.uploadFile(bytes, file.getOriginalFilename(), childPath);
            log.info("userId" + "上传了文件:" + newFilename);
            return respString(ResultCode.Success, ResultCode.SUCCESS,
                    AesUtil2.encryptData(URLEncoder.encode(newFilename, StandardCharsets.UTF_8.name())));
        } catch (Exception e) {
            log.error("上传附件失败", e);
            return respString(ResultCode.Fail, e.getMessage(), null);
        }
    }

    /**
     * 公共脚本上传
     *
     * @param childPath 路径
     * @param auth      验证
     * @return
     */
    @RequestMapping(value = "/uploadCommonScript", consumes = "multipart/form-data")
    public String uploadCommonScript(@RequestParam("file") MultipartFile file,
                                     @RequestParam(name = "auth") String auth,
                                     @RequestParam(name = "childPath") String childPath) {


        auth = AesUtil2.decryptAES2(auth);
        childPath = AesUtil2.decryptAES2(childPath);
        if (StrUtil.isBlank(auth) || StrUtil.isBlank(childPath)) {
            return respString(ResultCode.ParamIllegal, "参数出错", null);
        }

        log.info(auth);
        log.info(childPath);
        if (!childPath.startsWith(FileCode.COMPATIBILITY_SCRIPT)) {
            return respString(ResultCode.ParamIllegal, "上传路径不匹配", null);
        }

        String uploadPath = uploadUrl + childPath + File.separator;
        // 如果目录存在不能上传
        File uploadDir = new File(uploadPath);
        if (uploadDir.isDirectory()) {
            return respString(ResultCode.ParamIllegal, "不能重复上传", null);
        }
        FileAuth fileAuth = new FileAuth();
        fileAuth.setAuth(auth);
        fileAuth.setAuthType(FileCode.UPLOAD_COMMON_SCRIPT);
        fileAuth.setPath(uploadPath);
        authService.auth(fileAuth);

        try {
            byte[] bytes = file.getBytes();
            if (!"zip".equals(FileUtils.checkFileType(bytes))) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "只能上传ZIP压缩包");
            }
            double i = (bytes.length / (1024.0));
            //大于10M,不行
            if (i > 1024 * 10) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "上传文件不能大于10M");
            }

            String newFilename = fileUtils.uploadFile(bytes, file.getOriginalFilename(), childPath);
            log.info("userId" + "上传了文件:" + newFilename + " path = " + newFilename);
            return respString(ResultCode.Success, ResultCode.SUCCESS,
                    AesUtil2.encryptData(URLEncoder.encode(newFilename, StandardCharsets.UTF_8.name())));
        } catch (Exception e) {
            log.error("上传附件失败", e);
            return respString(ResultCode.Fail, e.getMessage(), null);
        }
    }

    /**
     * 公共脚本下载
     *
     * @param response 响应
     * @param childPath 加密的文件路径
     * @throws IOException
     */
    @RequestMapping(value = "/downloadCommonScript", produces = GlobalContext.PRODUCES)
    public void downloadCommonScript(
            HttpServletResponse response,
            @RequestParam(name = "auth") String auth,
            @RequestParam(name = "childPath") String childPath) throws IOException {


        auth = AesUtil2.decryptAES2(auth);
        childPath = AesUtil2.decryptAES2(childPath);
        log.info(auth);
        log.info(childPath);
        if (childPath == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "路径解密出错");
        }

        String filePath = URLDecoder.decode(childPath, StandardCharsets.UTF_8.name());
        if (!filePath.startsWith(uploadUrl)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "错误的路径");
        }

        FileAuth fileAuth = new FileAuth();
        fileAuth.setAuth(auth);
        fileAuth.setAuthType(FileCode.DOWNLOAD_COMMON_SCRIPT);
        fileAuth.setPath(filePath);
        authService.auth(fileAuth);

        //文件不存在返回
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "文件不存在");
        }
        String fileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name());
        response.setContentType(OCTET);
        response.setHeader(DISPOSITION, FILENAME + fileName);
        responseFile(response, file);
    }

    private void responseFile(HttpServletResponse response, File file) {
        try (ServletOutputStream output = response.getOutputStream();
             BufferedOutputStream buff = new BufferedOutputStream(output);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(bytes)) != -1) {
                buff.write(bytes, 0, bytesRead);
                buff.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
