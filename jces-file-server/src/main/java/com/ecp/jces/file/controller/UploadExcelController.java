package com.ecp.jces.file.controller;

import com.ecp.jces.code.FileCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.file.service.auth.AuthService;
import com.ecp.jces.file.util.ExcelUtil;
import com.ecp.jces.file.util.FileUtils;
import com.ecp.jces.file.util.StrUtil;
import com.ecp.jces.form.extra.FileAuth;
import com.ecp.jces.global.GlobalContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(value = "/file")
public class UploadExcelController extends Base {
    @Value("${param.jces.uploadUrl}")
    private String uploadUrl;

    private static final String DISPOSITION = "Content-Disposition";

    public static final String OCTET = "application/octet-stream; charset=UTF-8";

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/uploadExcel", consumes = "multipart/form-data")
    public String uploadExcel(@RequestParam("file") MultipartFile file,
                              @RequestParam(name = "auth") String auth,
                              @RequestParam(name = "childPath") String chlidPath) {
        auth = AesUtil2.decryptAES2(auth);
        chlidPath = AesUtil2.decryptAES2(chlidPath);
        if (StrUtil.isBlank(auth) || StrUtil.isBlank(chlidPath)) {
            return respString(ResultCode.ParamIllegal, "参数错误", null);
        }
        if (!FileCode.EXCEL.equals(chlidPath)) {
            return respString(ResultCode.ParamIllegal, "上传路径不匹配", null);
        }

        FileAuth fileAuth = new FileAuth();
        fileAuth.setAuth(auth);
        authService.auth(fileAuth);

        try {
            byte[] bytes = file.getBytes();
            if (!FileUtils.checkFileType(bytes).equals("Word/Excel")) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "上传Excel文件");
            }
            double I = bytes.length / 1024.0;
            if (I > 1024 * 100) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "文件大小不能超过100MB");
            }
            InputStream is = file.getInputStream();
            String[][] array = ExcelUtil.readExcelContent(is);
            return respString(ResultCode.Success, ResultCode.SUCCESS, array);
        } catch (Exception e) {
            return respString(ResultCode.Fail, e.getMessage(), null);
        }
    }

    // Excel文档下载
    @RequestMapping(value = "/downloadExcel", produces = GlobalContext.PRODUCES)
    public void downloadExcel(HttpServletResponse response,
                              @RequestParam(name = "childPath") String childPath) throws UnsupportedEncodingException {
        childPath = AesUtil2.decryptAES2(childPath);
        if (childPath == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "路径解析失败");
        }
        String filePath = uploadUrl + FileCode.API + File.separator + FileCode.DENY_API_MODEL;
        File file = new File(filePath);
        String fileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name());
        response.setContentType(OCTET);
        response.setHeader(DISPOSITION, "filename=" + fileName);
        try (ServletOutputStream output = response.getOutputStream();
             BufferedOutputStream buffer = new BufferedOutputStream(output);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytes = new byte[1024];
            int byteRead;
            while ((byteRead = bufferedInputStream.read(bytes)) != -1) {
                buffer.write(bytes, 0, byteRead);
                buffer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 下载第三方检测报告
    @RequestMapping(value = "/downloadReport", produces = GlobalContext.PRODUCES)
    public void downloadReport(HttpServletResponse response,
                               @RequestParam(name = "key") String key) throws UnsupportedEncodingException {
        key = AesUtil2.decryptAES2(key);
        if (key == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "路径解析失败");
        }
        String filePath = key;
        File file = new File(filePath);
        String fileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name());
        response.setContentType(OCTET);
        response.setHeader(DISPOSITION, "filename=" + fileName);
        try (ServletOutputStream output = response.getOutputStream();
             BufferedOutputStream buffer = new BufferedOutputStream(output);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytes = new byte[1024];
            int byteRead;
            while ((byteRead = bufferedInputStream.read(bytes)) != -1) {
                buffer.write(bytes, 0, byteRead);
                buffer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
