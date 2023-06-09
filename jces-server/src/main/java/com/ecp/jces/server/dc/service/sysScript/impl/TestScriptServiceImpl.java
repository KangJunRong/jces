package com.ecp.jces.server.dc.service.sysScript.impl;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.AppletForm;
import com.ecp.jces.form.TestScriptForm;
import com.ecp.jces.form.TestTaskForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.script.TestScriptMapper;
import com.ecp.jces.server.dc.service.applet.AppletService;
import com.ecp.jces.server.dc.service.applet.TestTaskService;
import com.ecp.jces.server.dc.service.sysScript.TestScriptService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.AppletVo;
import com.ecp.jces.vo.TestScriptVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

@Service
public class TestScriptServiceImpl implements TestScriptService {

    private static final Logger log = LoggerFactory.getLogger(TestScriptServiceImpl.class);
    @Autowired
    private TestScriptMapper dao;
    @Autowired
    private AppletService appletService;
    @Autowired
    private TestTaskService testTaskService;

    @Override
    public Pagination<TestScriptVo> page(TestScriptForm testScriptForm) throws FrameworkRuntimeException {
        Page<Object> pageHelper = PageHelper.startPage(testScriptForm.getPage(), testScriptForm.getPageCount());
        List<TestScriptVo> list = dao.findList(testScriptForm);
        Pagination<TestScriptVo> pagination = new Pagination<>(testScriptForm.getPage(), testScriptForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void add(TestScriptForm testScriptForm) throws FrameworkRuntimeException {
        testScriptForm.setPath(AesUtil2.decryptAES2(testScriptForm.getPath()));
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testScriptForm.setStatus(TestScriptVo.STATUS_NOT_ACTIVE);
        testScriptForm.setType(TestScriptVo.COMPATIBILITY_TEST_SCRIPT);
        testScriptForm.setId(StrUtil.newGuid());
        testScriptForm.setCreateUser(vo);
        testScriptForm.setCreateDate(date);
        testScriptForm.setUpdateUser(vo);
        testScriptForm.setUpdateDate(date);
        testScriptForm.setDelFlg(ResultCode.NOT_DEL);
        try {
            testScriptForm.setName(StrUtil.getFileNameFromUrl(testScriptForm.getPath()));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            testScriptForm.setName("");
        }
        dao.insert(testScriptForm);
    }

    @Override
    public void delete(TestScriptForm testScriptForm) throws FrameworkRuntimeException {
        TestTaskForm testTaskForm = new TestTaskForm();
        testTaskForm.setTestScriptId(testScriptForm.getId());
        testTaskForm.setDelFlg(ResultCode.NOT_DEL);
        int testTaskCount = testTaskService.count(testTaskForm);
        if(testTaskCount > 0){
            throw new FrameworkRuntimeException(ResultCode.Fail, "此通用脚本已经被测试过，不容许删除!");
        }
        testScriptForm.setDelFlg(ResultCode.DEL);
        dao.delete(testScriptForm);
    }

    @Override
    public void active(TestScriptForm testScriptForm) throws FrameworkRuntimeException {

        AppletForm appletForm = new AppletForm();
        appletForm.setStatus(ConstantCode.APPLET_STATUS_TESTING);
        appletForm.setDelFlg(ResultCode.NOT_DEL);
        List<AppletVo> appletInTestingList = appletService.list(appletForm);
        if(appletInTestingList != null && appletInTestingList.size() > 0){
            throw new FrameworkRuntimeException(ResultCode.Fail, "有应用正在测试中，不容许激活!");
        }
        //激活当前脚本
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testScriptForm.setStatus(TestScriptVo.STATUS_ACTIVE);
        testScriptForm.setActiveDate(date);
        testScriptForm.setUpdateUser(vo);
        testScriptForm.setUpdateDate(date);
        dao.updateStatus(testScriptForm);
        //设置其他处于激活状态的脚本未非激活状态
        dao.changeOtherActiveStatusToNotActiveStatus(testScriptForm);

    }

    @Override
    public Integer getVersion() throws FrameworkRuntimeException {
        Integer newestVersion = dao.maxVersion();
        if(newestVersion == null){
            return 1;
        }
        return newestVersion + 1;
    }
}
