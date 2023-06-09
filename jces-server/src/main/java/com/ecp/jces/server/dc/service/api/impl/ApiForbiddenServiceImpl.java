package com.ecp.jces.server.dc.service.api.impl;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.ApiForbiddenForm;
import com.ecp.jces.form.ApiLinkForm;
import com.ecp.jces.form.ApiLinkIdForm;
import com.ecp.jces.form.LicenceCodeForm;
import com.ecp.jces.server.dc.mapper.api.ApiForbiddenMapper;
import com.ecp.jces.server.dc.mapper.api.ApiLinkMapper;
import com.ecp.jces.server.dc.mapper.licenceCode.LicenceCodeMapper;
import com.ecp.jces.server.dc.service.api.ApiForbiddenService;
import com.ecp.jces.server.dc.service.licenceCode.LicenceCodeService;
import com.ecp.jces.server.util.BeanUtils;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.ApiForbiddenVo;
import com.ecp.jces.vo.ApiLinkVo;
import com.ecp.jces.vo.LicenceCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ApiForbiddenServiceImpl implements ApiForbiddenService {
    @Autowired
    private ApiForbiddenMapper apiForbiddenMapper;
    @Autowired
    private ApiLinkMapper apiLinkMapper;
    @Autowired
    private LicenceCodeMapper licenceCodeMapper;
    @Autowired
    private LicenceCodeService licenceCodeService;

    public List<ApiForbiddenVo> list(ApiForbiddenForm form){
        return apiForbiddenMapper.list(form);
    }

    public List<ApiLinkVo> getForbiddenByRole(ApiLinkForm form) {
        return apiLinkMapper.getForbiddenByRole(form);
    }

    @Transactional(readOnly = false,rollbackFor = FrameworkRuntimeException.class)
    public void pushLink(ApiLinkForm form) {
        apiLinkMapper.getNew(form.getApiRoleId());
        List<ApiLinkIdForm> list = new ArrayList<>();
        if (form.getForbiddenIdList().size() > 0) {
            for (String forbiddenId: form.getForbiddenIdList()) {
                ApiLinkIdForm IdForm = new ApiLinkIdForm();
                IdForm.setId(StrUtil.newGuid());
                IdForm.setForbiddenId(forbiddenId);
                list.add(IdForm);
            }
            form.setIdList(list);
            apiLinkMapper.pushLink(form);
        }    
        List<LicenceCodeVo> licenceCodeList = licenceCodeMapper.findByApiRoleId(form.getApiRoleId());
        List<String> forbiddenId = form.getForbiddenIdList();
        if(licenceCodeList.size()>0)
        {
            for (LicenceCodeVo vo : licenceCodeList) {
                vo.setLicenceCode(licenceCodeService.apiResetLicenceCode(BeanUtils.copy(vo, LicenceCodeForm.class),forbiddenId));
            }
            licenceCodeMapper.updateLicenceCodeByApiRoleId(BeanUtils.copy(licenceCodeList,LicenceCodeForm.class));
        }
    }

    @Transactional(readOnly = false , rollbackFor = FrameworkRuntimeException.class)
    public void resetForbidden(String[][] data) {
        if(data.length <= 1){
            throw new FrameworkRuntimeException(ResultCode.Fail, "excel中没有数据");
        }
        apiForbiddenMapper.reset();
        List<ApiForbiddenForm> list = new ArrayList<>();
        if(data[1][0]!=null) {
            for (int i = 1; i < data.length; i++) {
                ApiForbiddenForm form = new ApiForbiddenForm();
                form.setId(StrUtil.newGuid());
                form.setVersionNo(null);
                form.setPackageName(data[i][0]);
                form.setClassName(data[i][1]);
                form.setMethodName(data[i][2]);
                form.setDescriptor(data[i][3]);
                list.add(form);
            }
            apiForbiddenMapper.pushAll(list);
        }
    }
}
