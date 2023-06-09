package com.ecp.jces.server.dc.service.applet.impl;

import com.ecp.jces.code.ConfigKey;
import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.*;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.applet.AppletMapper;
import com.ecp.jces.server.dc.mapper.applet.TestBusinessScriptMapper;
import com.ecp.jces.server.dc.mapper.script.TestScriptMapper;
import com.ecp.jces.server.dc.mapper.sys.SysConfigMapper;
import com.ecp.jces.server.dc.mapper.task.TestScheduleMapper;
import com.ecp.jces.server.dc.mapper.task.TestTaskMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixMapper;
import com.ecp.jces.server.dc.service.applet.TestBusinessScriptService;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;


@Service
public class TestBusinessScriptServiceImpl implements TestBusinessScriptService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestBusinessScriptServiceImpl.class);
    @Value("${applet.test.commitTestMax}")
    public Integer commitTestMax;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private TestBusinessScriptMapper testBusinessScriptMapper;
    @Autowired
    private AppletMapper appletMapper;
    @Autowired
    private TestTaskMapper testTaskMapper;
    @Autowired
    private TestScriptMapper testScriptMapper;

    @Autowired
    private TestMatrixMapper testMatrixMapper;

    @Override
    public TestBusinessScriptVo findByAppletId(String appletId) throws FrameworkRuntimeException {
        return testBusinessScriptMapper.findByAppletId(appletId);
    }

    @Override
    public TestBusinessScriptVo findById(String testBusinessScriptId) throws FrameworkRuntimeException {
        return testBusinessScriptMapper.findById(testBusinessScriptId);
    }

    @Override
    public List<TestBusinessScriptVo> list(TestBusinessScriptForm testBusinessScriptForm) throws FrameworkRuntimeException {

        testBusinessScriptForm.setDelFlg(ResultCode.NOT_DEL);
        return testBusinessScriptMapper.list(testBusinessScriptForm);
    }

    @Override
    public Pagination<TestBusinessScriptVo> page(TestBusinessScriptForm form) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        if (ConstantCode.ROLE_DEVELOPER.equals(vo.getRoleId())) {
            form.setCreateUser(vo.getId());
        }
        if (form.getAppletName() != null) {
            form.setAppletName(form.getAppletName().trim());
        }
        Page<Object> pageHelper = PageHelper.startPage(form.getPage(), form.getPageCount());
        form.setDelFlg(ResultCode.NOT_DEL);
        List<TestBusinessScriptVo> list = testBusinessScriptMapper.list(form);
        Pagination<TestBusinessScriptVo> pagination = new Pagination<>(form.getPage(), form.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void add(TestBusinessScriptForm testBusinessScriptForm) throws FrameworkRuntimeException {
        testBusinessScriptForm.setPath(AesUtil2.decryptAES2(testBusinessScriptForm.getPath()));
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testBusinessScriptForm.setStatus(ConstantCode.APPLET_STATUS_NOT_COMMIT);
        testBusinessScriptForm.setId(StrUtil.newGuid());
        testBusinessScriptForm.setCreateUser(vo.getId());
        testBusinessScriptForm.setCreateDate(date);
        testBusinessScriptForm.setUpdateUser(vo.getId());
        testBusinessScriptForm.setUpdateDate(date);
        testBusinessScriptForm.setDelFlg(ResultCode.NOT_DEL);

        try {
            testBusinessScriptForm.setName(StrUtil.getFileNameFromUrl(testBusinessScriptForm.getPath()));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
            testBusinessScriptForm.setName("");
        }
        testBusinessScriptMapper.add(testBusinessScriptForm);

        AppletForm appletForm = new AppletForm();
        appletForm.setId(testBusinessScriptForm.getAppletId());
        appletForm.setUpdateUser(vo.getId());
        appletForm.setUpdateDate(date);
        appletForm.setTestBusinessScriptId(testBusinessScriptForm.getId());
        appletForm.setBusinessScriptLastVersion(testBusinessScriptForm.getVersion());
        appletMapper.edit(appletForm);

    }

    @Override
    public void edit(TestBusinessScriptForm testBusinessScriptForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testBusinessScriptForm.setUpdateUser(vo.getId());
        testBusinessScriptForm.setUpdateDate(date);
        testBusinessScriptMapper.edit(testBusinessScriptForm);
    }

    @Override
    public void del(TestBusinessScriptForm testBusinessScriptForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        testBusinessScriptForm.setUpdateUser(vo.getId());
        testBusinessScriptForm.setUpdateDate(date);
        testBusinessScriptForm.setDelFlg(ResultCode.DEL);
        testBusinessScriptForm.setPath(null);
        testBusinessScriptMapper.edit(testBusinessScriptForm);
        if (StrUtil.isNotBlank(testBusinessScriptForm.getAppletId())) {
            appletMapper.delBusinessScript(testBusinessScriptForm.getAppletId());
        }

    }

    @Override
    public TestBusinessScriptVo getLastVersion(String appletId) throws FrameworkRuntimeException {
        return testBusinessScriptMapper.getLastVersion(appletId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void commitPretest(String appletId) throws FrameworkRuntimeException {
        /*条件限制：测试任务过多也不能提交*/
        if (!checkCanPreTest()) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "提交测试个数过多");
        }

        TestBusinessScriptVo testBusinessScriptVo = testBusinessScriptMapper.findByAppletId(appletId);
        if (testBusinessScriptVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "请先上传自定义脚本");
        }
        //状态是测试中的不能再提交
        if (ConstantCode.SCRIPT_STATUS_TESTING.equals(testBusinessScriptVo.getStatus())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "状态是测试中的不能再提交测试");
        }
        AppletVo appletVo = appletMapper.findById(appletId);
        if (appletVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "找不到该应用");
        }
        if (appletVo.getVersionId() == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "请先上传cap");
        }


        TestScriptVo testScriptVo = testScriptMapper.getActive();
        if (testScriptVo == null || StrUtil.isBlank(testScriptVo.getPath())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "没有激活通用脚本,不能测试");
        }

        //有空闲矩阵才可以进行业务测试
        TestMatrixForm testMatrixForm = new TestMatrixForm();
        testMatrixForm.setMatrixStatus(TestMatrixVo.FREE_STATUS);
        testMatrixForm.setDelFlg(ResultCode.NOT_DEL);
        testMatrixForm.setVersionNo(appletVo.getVersionNo());
        if (testMatrixMapper.findFreeList(testMatrixForm).size() == 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "没有空闲的" + appletVo.getVersionNo() + "测试矩阵,不能测试");
        }

        UserVo userVo = AuthCasClient.getUser();
        Date date = new Date();

        TestTaskForm testTaskForm = new TestTaskForm();
        testTaskForm.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
        testTaskForm.setId(StrUtil.newGuid());
        testTaskForm.setType(ConstantCode.TEST_TASK_TYPE_PRETEST);
        testTaskForm.setTestScriptId(testScriptVo.getId());
        testTaskForm.setAppletId(appletId);
        testTaskForm.setAppletVersionId(appletVo.getVersionId());
        testTaskForm.setTestBusinessScriptId(testBusinessScriptVo.getId());
        testTaskForm.setCreateUser(userVo.getId());
        testTaskForm.setCreateDate(date);
        testTaskForm.setUpdateUser(userVo.getId());
        testTaskForm.setUpdateDate(date);
        testTaskForm.setDelFlg(ResultCode.NOT_DEL);

        //变更自定义脚本状态
        TestBusinessScriptForm testBusinessScriptForm = new TestBusinessScriptForm();
        testBusinessScriptForm.setId(testBusinessScriptVo.getId());
        testBusinessScriptForm.setStatus(ConstantCode.SCRIPT_STATUS_TESTING);
        testBusinessScriptForm.setTestStart(date);

        testBusinessScriptMapper.edit(testBusinessScriptForm);
        testTaskMapper.add(testTaskForm);
    }

    private Boolean checkCanPreTest() {
        UserVo user = AuthCasClient.getUser();
        //管理员直接可以过
        if (ConstantCode.ROLE_ADMINISTRATORS.equals(user.getRoleId())) {
            return true;
        }
        //查找出有多少个正在测试和待测试的应用
        int passCount = commitTestMax;
        SysConfigForm sysConfigForm = new SysConfigForm();
        sysConfigForm.setLabel(ConfigKey.CommitTestMax);
        SysConfigVo sysConfigVo = sysConfigMapper.getByLabel(sysConfigForm);
        if (sysConfigVo != null && sysConfigVo.getValue() != null) {
            try {
                passCount = Integer.parseInt(sysConfigVo.getValue());
            } catch (Exception e) {
                LOGGER.info("commitTestMax参数转换出错");
            }
        }
        int count = appletMapper.findTestCountByUserId(user.getId());
        return passCount > count;
    }
}
