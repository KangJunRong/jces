package com.ecp.jces.server.dc.service.async.impl;

import com.eastcompeace.capAnalysis.CapAnalysisUtil;
import com.eastcompeace.capAnalysis.doman.API;
import com.eastcompeace.capAnalysis.doman.AnalysResult;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.form.ApiSaveRecordForm;
import com.ecp.jces.form.extra.StartTestForm;
import com.ecp.jces.server.dc.mapper.api.ApiForbiddenSaveMapper;
import com.ecp.jces.server.dc.mapper.api.ApiSaveRecordMapper;
import com.ecp.jces.server.dc.mapper.applet.AppletVersionMapper;
import com.ecp.jces.server.dc.mapper.applet.TestCheckReportMapper;
import com.ecp.jces.server.dc.mapper.user.UserMapper;
import com.ecp.jces.server.dc.service.async.AsyncService;
import com.ecp.jces.server.util.BeanUtils;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.ApiForbiddenSaveVo;
import com.ecp.jces.vo.AppletVersionVo;
import com.ecp.jces.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class AsyncServiceImpl implements AsyncService {
    @Autowired
    private AppletVersionMapper appletVersionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApiForbiddenSaveMapper apiForbiddenSaveMapper;

    @Autowired
    private ApiSaveRecordMapper apiSaveRecordMapper;

    @Autowired
    private TestCheckReportMapper testCheckReportMapper;

    @Value("${param.expFilePath}")
    private String expFilePath;

    private static final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);


    @Override
    public void handleSaveApi(StartTestForm testForm) {
        if (apiSaveRecordMapper.findByTaskId(testForm.getTestTaskId()) != null) {
            return;
        }
        AppletVersionVo vo = appletVersionMapper.findById(testForm.getAppletVersionId());
        UserVo userVo = userMapper.findById(vo.getCreateUser());

        boolean flag = true;
        //判断是否安全审核员
        if (StrUtil.isBlank(userVo.getTemplateId())) {
            flag = false;
        }

        List<ApiForbiddenSaveVo> list = apiForbiddenSaveMapper.list();
        if (list.size() == 0) {
            return;
        }

        List<API> disableApis = BeanUtils.copy(list, API.class);

        try {
            String capFilePath = new File(URLDecoder.decode(vo.getCapPath(), StandardCharsets.UTF_8.name())).getParent()
                    + File.separator + "jcesCapTemp";
            AnalysResult result = CapAnalysisUtil.checkCap(capFilePath, expFilePath, disableApis);
            if (0 == result.getResult()) {
                if (result.getDisabledApis() != null && result.getDisabledApis().size() > 0) {
                    StringBuilder apiData = new StringBuilder();
                    for (String api : result.getDisabledApisMethodStrs()) {
                        apiData.append(api).append(",");

                    }
                    apiData.deleteCharAt(apiData.length() - 1);

                    if (flag) {
                        ApiSaveRecordForm form = new ApiSaveRecordForm();
                        form.setTaskId(testForm.getTestTaskId());
                        form.setApis(apiData.toString());
                        form.setCreateDate(new Date());
                        apiSaveRecordMapper.insert(form);
                    }

                    //敏感API
                    testCheckReportMapper.updateSensitiveApi(testForm.getAppletId(),
                            testForm.getAppletVersionId(),JSONUtils.toJSONString(apiData.toString().split(",")));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
