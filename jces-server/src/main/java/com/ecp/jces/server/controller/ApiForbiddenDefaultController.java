package com.ecp.jces.server.controller;

import com.ecp.jces.code.ResultCode;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.ApiDownLoadForm;
import com.ecp.jces.form.ApiForbiddenDefaultForm;
import com.ecp.jces.form.ApiForbiddenForm;
import com.ecp.jces.form.ApiLinkForm;
import com.ecp.jces.global.GlobalContext;
import com.ecp.jces.server.dc.mapper.api.ApiForbiddenDefaultMapper;
import com.ecp.jces.server.dc.service.api.ApiForbiddenService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.server.util.VerificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/apiForbiddenDefault")
public class ApiForbiddenDefaultController extends Base{
    @Autowired
    private ApiForbiddenDefaultMapper mapper;

    @PostMapping(value = "/list" ,produces = GlobalContext.PRODUCES)
    public String getForbiddenList(@RequestBody ApiForbiddenForm form){
        return respString(ResultCode.Success,ResultCode.SUCCESS,mapper.list());
    }

    @PostMapping(value = "/add" , produces = GlobalContext.PRODUCES)
    public String add(@RequestBody ApiDownLoadForm form){
        VerificationUtils.objList("defaultList",form.getDefaultList());
        if(form.getDefaultList().size() == 0){
            throw new FrameworkRuntimeException(ResultCode.Fail, "excel中没有数据");
        }
        mapper.deleteAll();
        for (ApiForbiddenDefaultForm defaultForm : form.getDefaultList()){
            defaultForm.setId(StrUtil.newGuid());
        }
        mapper.insert(form.getDefaultList());
        return respString(ResultCode.Success,ResultCode.SUCCESS,null);
    }
}
