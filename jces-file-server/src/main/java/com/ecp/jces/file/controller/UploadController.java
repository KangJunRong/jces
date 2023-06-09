package com.ecp.jces.file.controller;

import com.ecp.jces.code.FileCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.file.service.auth.AuthService;
import com.ecp.jces.file.util.FileHelper;
import com.ecp.jces.file.util.FileUtils;
import com.ecp.jces.file.util.StrUtil;
import com.ecp.jces.form.extra.FileAuth;
import com.ecp.jces.form.extra.VmCos;
import com.ecp.jces.global.GlobalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件管理
 */
@RestController
@RequestMapping("/file")
public class UploadController extends Base {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private FileUtils fileUtils;

    @Value("${param.jces.uploadUrl}")
    private String uploadUrl;

    private static final String DISPOSITION = "Content-Disposition";

    private static final String FILENAME = "filename=";

    public static final String OCTET = "application/octet-stream; charset=UTF-8";
    @Autowired
    private AuthService authService;

    /**
     * COS文档上传
     *
     * @param childPath 路径
     * @param auth      验证
     * @return
     */
    @PostMapping(value = "/uploadCos", consumes = "multipart/form-data")
    public String uploadCos(@RequestBody MultipartFile file,
                            @RequestParam(name = "auth") String auth,
                            @RequestParam(name = "childPath") String childPath) {
        auth = AesUtil2.decryptAES2(auth);
        childPath = AesUtil2.decryptAES2(childPath);
        if (StrUtil.isBlank(auth) || StrUtil.isBlank(childPath)) {
            return respString(ResultCode.ParamIllegal, "参数出错", null);
        }
        log.info(auth);
        log.info(childPath);
        if (!FileCode.COS.equals(childPath)) {
            return respString(ResultCode.ParamIllegal, "上传路径不匹配", null);
        }
        String uploadPath = uploadUrl + childPath + File.separator;
        FileAuth fileAuth = new FileAuth();
        fileAuth.setAuth(auth);
        fileAuth.setAuthType(FileCode.UPLOAD_EXE);
        fileAuth.setPath(uploadPath);
        authService.auth(fileAuth);

        try {
            byte[] bytes = file.getBytes();

            if (!"zip".equals(FileUtils.checkFileType(bytes))) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "请上传zip压缩文件");
            }
            double i = (bytes.length / (1024.0));
            //大于10M,不行
            if (i > 1024 * 100) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "上传文件不能大于100M");
            }

            String hash = FileHelper.hash(bytes);
            childPath += File.separator + hash;
            log.info(childPath);

            String newFilename = fileUtils.uploadExe(bytes, file.getOriginalFilename(), childPath);
            log.info("userId" + "上传了文件:" + newFilename + " path = " + newFilename);

            return respString(ResultCode.Success, ResultCode.SUCCESS,
                    AesUtil2.encryptData(URLEncoder.encode(newFilename, StandardCharsets.UTF_8.name())));
        } catch (Exception e) {
            log.error("上传附件失败", e);
            return respString(ResultCode.Fail, e.getMessage(), null);
        }
    }

    /**
     * COS下载
     *
     * @param response 响应
     * @param form COS信息
     * @throws IOException
     */
    @RequestMapping(value = "/downloadCos", produces = GlobalContext.PRODUCES)
    public void downloadCos(HttpServletRequest request,
                            HttpServletResponse response,
                            @RequestBody VmCos form) throws IOException{

        String ip = request.getHeader("X-Real-IP");
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }

        if(StrUtil.isBlank(form.getFileId())){
            throw new FrameworkRuntimeException(ResultCode.Fail, "fileId不能为空");
        }
        if(StrUtil.isBlank(form.getVersionNo())){
            throw new FrameworkRuntimeException(ResultCode.Fail, "cos版本不能为空");
        }
        if(form.getNo() == null){
            throw new FrameworkRuntimeException(ResultCode.Fail, "小版本号不能为空");
        }

        if(StrUtil.isBlank(form.getMatrixId())&&StrUtil.isBlank(form.getMachineCode())){
            throw new FrameworkRuntimeException(ResultCode.Fail, "机器码和矩阵ID必填其一");
        }

        String childPath = AesUtil2.decryptAES2(form.getFileId());
        if (childPath == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "路径解密出错");
        }

        String filePath = URLDecoder.decode(childPath, StandardCharsets.UTF_8.name());
        if (!filePath.startsWith(uploadUrl + FileCode.COS)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "错误的路径");
        }

        form.setIp(ip);
        authService.vmCosDownAuth(form);

        //文件不存在返回
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "文件不存在");
        }
        String fileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name());
        response.setContentType(OCTET);
        response.setHeader(DISPOSITION, FILENAME + fileName);
        response.setContentLength(Integer.parseInt(String.valueOf(file.length())));
        responseFile(response, file);
    }

    private void responseFile(HttpServletResponse response, File file) {
        try (ServletOutputStream output = response.getOutputStream();
             BufferedOutputStream buff = new BufferedOutputStream(output);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytes = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = bufferedInputStream.read(bytes)) != -1) {
                buff.write(bytes, 0, bytesRead);
                buff.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 文档上传
     *
     * @param childPath 路径
     * @param auth      验证
     * @return
     */
    @RequestMapping(value = "/uploadFile", consumes = "multipart/form-data")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(name = "auth") String auth,
                             @RequestParam(name = "childPath") String childPath) {


        auth = AesUtil2.decryptAES2(auth);
        childPath = AesUtil2.decryptAES2(childPath);
        if (StrUtil.isBlank(auth) || StrUtil.isBlank(childPath)) {
            return respString(ResultCode.ParamIllegal, "参数出错", null);
        }

        log.info(auth);
        log.info(childPath);
        if (!FileCode.SPECIFICATION.equals(childPath)) {
            return respString(ResultCode.ParamIllegal, "上传路径不匹配", null);
        }
        String uploadPath = uploadUrl + childPath + File.separator;
        FileAuth fileAuth = new FileAuth();
        fileAuth.setAuth(auth);
        fileAuth.setAuthType(FileCode.UPLOAD_FILE);
        fileAuth.setPath(uploadPath);
        authService.auth(fileAuth);

        try {
            byte[] bytes = file.getBytes();

            if (FileUtils.checkFileType(bytes) == null || FileUtils.checkFileType(bytes).equals("exe")) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "上传PDF、DOC、DOCX、XLS、XLSX、PPT、PPTX、ZIP类型的文档文件");
            }

            double i = (bytes.length / (1024.0));
            //大于10M,不行
            if (i > 1024 * 10) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "上传文件不能大于10M");
            }

            childPath += File.separator + StrUtil.newGuid();
            log.info(childPath);

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
     * 文档下载
     *
     * @param response
     * @param childPath
     * @throws IOException
     */
    @RequestMapping(value = "/downloadFile", produces = GlobalContext.PRODUCES)
    public void downloadFile(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "childPath") String childPath) throws IOException {
        childPath = AesUtil2.decryptAES2(childPath);
        if (childPath == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "路径解密出错");
        }

        String filePath = URLDecoder.decode(childPath, StandardCharsets.UTF_8.name());
        if (!filePath.startsWith(uploadUrl + FileCode.SPECIFICATION)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "错误的路径");
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
     * 日志下载
     *
     * @param response
     * @param childPath
     * @throws IOException
     */
    @RequestMapping(value = "/downloadLog", produces = GlobalContext.PRODUCES)
    public void downloadLog(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "auth") String auth,
            @RequestParam(name = "childPath") String childPath) throws IOException {

        auth = AesUtil2.decryptAES2(auth);
        childPath = AesUtil2.decryptAES2(childPath);
        if (childPath == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "路径解密出错");
        }

        String filePath = URLDecoder.decode(childPath, StandardCharsets.UTF_8.name());
        if (!filePath.startsWith(uploadUrl)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "错误的路径");
        }

        FileAuth fileAuth = new FileAuth();
        fileAuth.setAuth(auth);
        fileAuth.setAuthType(FileCode.DOWNLOAD_LOG);
        fileAuth.setPath(childPath);
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

    /**
     * 业务测试日志下载
     *
     * @param response 响应
     * @param childPath 加密的文件路径
     * @throws IOException
     */
    @RequestMapping(value = "/downloadBusiness", produces = GlobalContext.PRODUCES)
    public void downloadBusiness(
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
        fileAuth.setAuthType(FileCode.DOWNLOAD_BUSINESS);
        fileAuth.setPath(childPath);
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

    /**
     * cap下载
     *
     * @param response
     * @param childPath
     * @throws IOException
     */
    @RequestMapping(value = "/downloadCap", produces = GlobalContext.PRODUCES)
    public void downloadCap(
            HttpServletResponse response,
            @RequestParam(name = "childPath") String childPath) throws IOException {
        childPath = AesUtil2.decryptAES2(childPath);
        if (childPath == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "路径解密出错");
        }

        String filePath = URLDecoder.decode(childPath, StandardCharsets.UTF_8.name());
        if (!filePath.startsWith(uploadUrl + FileCode.CAP)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "错误的路径");
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
     * 下载公共测试脚本
     *
     * @param response 响应
     * @param childPath 加密的文件路径
     * @throws IOException
     */
    @RequestMapping(value = "/downloadTestScript", produces = GlobalContext.PRODUCES)
    public void downloadTestScript(
            HttpServletResponse response,
            @RequestParam(name = "childPath") String childPath) throws IOException {
        childPath = AesUtil2.decryptAES2(childPath);
        if (childPath == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "路径解密出错");
        }

        String filePath = URLDecoder.decode(childPath, StandardCharsets.UTF_8.name());
        if (!filePath.startsWith(uploadUrl + FileCode.COMPATIBILITY_SCRIPT)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "错误的路径");
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
     * 下载业务测试脚本
     *
     * @param response
     * @param childPath
     * @throws IOException
     */
    @RequestMapping(value = "/downloadBusinessScript", produces = GlobalContext.PRODUCES)
    public void downloadBusinessScript(
            HttpServletResponse response,
            @RequestParam(name = "childPath") String childPath) throws IOException {
        childPath = AesUtil2.decryptAES2(childPath);
        if (childPath == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "路径解密出错");
        }

        String filePath = URLDecoder.decode(childPath, StandardCharsets.UTF_8.name());
        if (!filePath.startsWith(uploadUrl + FileCode.CUSTOMIZE_SCRIPT)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "错误的路径");
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
}
