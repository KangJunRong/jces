package com.ecp.jces.server.dc.service.applet.impl;

import com.ecp.jces.code.ConfigKey;
import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.code.ThirdCode;
import com.ecp.jces.core.utils.AesUtil2;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.*;
import com.ecp.jces.jctool.capscript.AppletInstance;
import com.ecp.jces.jctool.capscript.ExeLoadFile;
import com.ecp.jces.jctool.capscript.ExeModule;
import com.ecp.jces.jctool.detection.model.PackageInfo;
import com.ecp.jces.jctool.exception.TlvAnalysisException;
import com.ecp.jces.jctool.util.GPUtil;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.cache.RedisDao;
import com.ecp.jces.server.dc.mapper.api.ApiSaveRecordMapper;
import com.ecp.jces.server.dc.mapper.applet.*;
import com.ecp.jces.server.dc.mapper.script.TestScriptMapper;
import com.ecp.jces.server.dc.mapper.sys.SysConfigMapper;
import com.ecp.jces.server.dc.mapper.sys.TemplateConfigMapper;
import com.ecp.jces.server.dc.mapper.task.TestReportApduMapper;
import com.ecp.jces.server.dc.mapper.task.TestReportDataMapper;
import com.ecp.jces.server.dc.mapper.task.TestReportMapper;
import com.ecp.jces.server.dc.mapper.task.TestTaskMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestEngineMapper;
import com.ecp.jces.server.dc.mapper.terminal.TestMatrixMapper;
import com.ecp.jces.server.dc.service.applet.AppletService;
import com.ecp.jces.server.third.CenterInf;
import com.ecp.jces.server.util.DateUtil;
import com.ecp.jces.server.util.PdfUtil;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itextpdf.text.pdf.PdfException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
public class AppletServiceImpl implements AppletService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppletServiceImpl.class);

    @Value("${applet.test.commitTestMax}")
    public Integer commitTestMax;

    @Value("${param.third.excel}")
    private String excelPath;

    @Value("${param.third.reportPath}")
    private String reportPath;

    @Value("${param.third.downloadUrl}")
    private String downloadUrl;

    @Autowired
    private AppletMapper appletMapper;
    @Autowired
    private AppletVersionMapper appletVersionMapper;
    @Autowired
    private TestBusinessScriptMapper testBusinessScriptMapper;
    @Autowired
    private TestTaskMapper testTaskMapper;

    @Autowired
    private TestScriptMapper testScriptMapper;

    @Autowired
    private AppletExeLoadFileMapper appletExeLoadFileMapper;

    @Autowired
    private AppletExeModuleMapper appletExeModuleMapper;

    @Autowired
    private TestReportMapper testReportMapper;
    @Autowired
    private TestCheckReportMapper testCheckReportMapper;
    @Autowired
    private TestReportApduMapper testReportApduMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private TestReportDataMapper testReportDataMapper;

    @Autowired
    private TestMatrixMapper testMatrixMapper;

    @Autowired
    private TestEngineMapper testEngineMapper;

    @Autowired
    private CenterInf centerInf;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private TemplateConfigMapper templateConfigMapper;

    @Autowired
    private ApiSaveRecordMapper apiSaveRecordMapper;

    @Override
    public AppletVo findById(String appletId) throws FrameworkRuntimeException {
        AppletVo vo = appletMapper.findById(appletId);
        if (StrUtil.isNotBlank(vo.getVersionId())) {
            List<AppletExeLoadFileVo> loadFiles = appletMapper.getLoadFiles(vo.getId(), vo.getVersionId());
            vo.setLoadFiles(loadFiles);
        }
        return vo;
    }

    @Override
    public List<AppletVo> list(AppletForm appletForm) throws FrameworkRuntimeException {

        appletForm.setDelFlg(ResultCode.NOT_DEL);
        return appletMapper.list(appletForm);
    }

    @Override
    public Pagination<AppletVo> page(AppletForm form) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        if (ConstantCode.ROLE_DEVELOPER.equals(vo.getRoleId())) {
            form.setCreateUser(vo.getId());
        }
        if (form.getName() != null) {
            form.setName(form.getName().trim());
        }
        Page<Object> pageHelper = PageHelper.startPage(form.getPage(), form.getPageCount());
        form.setDelFlg(ResultCode.NOT_DEL);
        List<AppletVo> list = appletMapper.list(form);
        Pagination<AppletVo> pagination = new Pagination<>(form.getPage(), form.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public void add(AppletForm appletForm) throws FrameworkRuntimeException {


        if (appletMapper.findByName(appletForm.getName()) != null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "该应用名称已经被使用");
        }

        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        appletForm.setStatus(ConstantCode.APPLET_STATUS_NOT_COMMIT);
        appletForm.setId(StrUtil.newGuid());
        appletForm.setCreateUser(vo.getId());
        appletForm.setCreateDate(date);
        appletForm.setUpdateUser(vo.getId());
        appletForm.setUpdateDate(date);
        appletForm.setDelFlg(ResultCode.NOT_DEL);
        appletMapper.add(appletForm);
    }

    @Override
    public void edit(AppletForm appletForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        appletForm.setUpdateUser(vo.getId());
        appletForm.setUpdateDate(date);
        appletMapper.edit(appletForm);
    }

    // 终止测试
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void terminateTest(AppletForm appletForm) throws FrameworkRuntimeException {
        TestTaskVo vo = testTaskMapper.findByApplet(appletForm);
        if (vo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "找不到该测试任务");
        }

        if (ConstantCode.APPLET_STATUS_TEST_SUCCESS.equals(vo.getStatus()) ||
                ConstantCode.APPLET_STATUS_TEST_FAIL.equals(vo.getStatus())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "测试已完成，请刷新页面");
        }

        //待测试状态则直接终止
        if (ConstantCode.APPLET_STATUS_WAITING_TEST.equals(vo.getStatus())) {
            String  matrixId = redisDao.getValue(ConstantCode.TASK_START + vo.getId());
            if (matrixId != null) {
                centerInf.terminateTest(testEngineMapper.findByMatrixId(matrixId), matrixId, vo.getId());
                redisDao.delValue(ConstantCode.TASK_START + vo.getId());
            }
            TestTaskForm testTaskForm = new TestTaskForm();
            testTaskForm.setId(vo.getId());
            testTaskForm.setStatus(ConstantCode.APPLET_STATUS_TEST_FAIL);
            testTaskForm.setTestEnd(new Date());
            testTaskMapper.edit(testTaskForm);

            AppletForm form = new AppletForm();
            form.setId(vo.getAppletId());
            form.setStatus(ConstantCode.APPLET_STATUS_TEST_FAIL);
            appletMapper.edit(form);

            AppletVersionForm appletVersionForm = new AppletVersionForm();
            appletVersionForm.setId(vo.getAppletVersionId());
            appletVersionForm.setStatus(ConstantCode.APPLET_STATUS_TEST_FAIL);
            appletVersionMapper.edit(appletVersionForm);
            return;
        }

        TestEngineVo engineVo = testEngineMapper.findByMatrixId(vo.getMatrixId());
        if (engineVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "没有找到矩阵所属的测试引擎");
        }

        if (!TestEngineVo.ONLINE_STATUS.equals(engineVo.getStatus())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "测试引擎不是在线状态不能终止");
        }
        centerInf.terminateTest(engineVo, vo.getMatrixId(), vo.getId());
    }

    @Override
    public void del(AppletForm appletForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        appletForm.setUpdateUser(vo.getId());
        appletForm.setUpdateDate(date);
        appletForm.setDelFlg(ResultCode.DEL);
        appletMapper.edit(appletForm);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void commitCap(AppletForm appletForm) throws FrameworkRuntimeException {

        String path = AesUtil2.decryptAES2(appletForm.getCapPath().getPath());
        if (StrUtil.isBlank(path)) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "cap包路径错误");
        }
        appletForm.getCapPath().setPath(path);

        UserVo vo = AuthCasClient.getUser();
        Date date = new Date();
        appletForm.setUpdateUser(vo.getId());
        appletForm.setUpdateDate(date);

        AppletVersionForm form = new AppletVersionForm();
        try {
            form.setName(StrUtil.getFileNameFromUrl(appletForm.getCapPath().getPath()));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
            form.setName("");
        }
        form.setVersion(appletForm.getLastVersion());
        form.setCapPath(appletForm.getCapPath().getPath());
        form.setStatus(ConstantCode.APPLET_STATUS_NOT_COMMIT);
        form.setId(StrUtil.newGuid());
        form.setCreateUser(vo.getId());
        form.setCreateDate(date);
        form.setUpdateUser(vo.getId());
        form.setUpdateDate(date);
        form.setDelFlg(ResultCode.NOT_DEL);
        form.setAppletId(appletForm.getId());
        form.setDescription(appletForm.getCapDesc());
        form.setExamine(ConstantCode.EXAMINE_STATUS_NOT_COMMIT);
        appletVersionMapper.add(form);

        appletForm.setVersionId(form.getId());
        appletForm.setUpdateDate(date);
        appletForm.setUpdateUser(vo.getId());
        appletForm.setStatus(ConstantCode.APPLET_STATUS_NOT_COMMIT);
        appletMapper.edit(appletForm);

        if (appletForm.getCapPath().getLoadFiles() != null && appletForm.getCapPath().getLoadFiles().size() > 0) {

            //插入数据库
            int i = 1;
            for (ExeLoadFile loadFile : appletForm.getCapPath().getLoadFiles()) {
                AppletExeLoadFileForm exeLoadFile = new AppletExeLoadFileForm();
                exeLoadFile.setId(StrUtil.newGuid());
                exeLoadFile.setCreateUser(vo.getId());
                exeLoadFile.setCreateDate(date);
                exeLoadFile.setUpdateUser(vo.getId());
                exeLoadFile.setUpdateDate(date);
                exeLoadFile.setHash(loadFile.getHash());
                exeLoadFile.setDelFlg(ResultCode.NOT_DEL);

                exeLoadFile.setAid(loadFile.getAid());
                exeLoadFile.setAppletId(appletForm.getId());
                exeLoadFile.setAppletVersionId(form.getId());
                exeLoadFile.setLoadSequence(i);
                exeLoadFile.setFileName(loadFile.getFileName());
                exeLoadFile.setType(loadFile.isLibPkg() ? ConstantCode.LOAD_FILE_TYPE_LIB : ConstantCode.LOAD_FILE_TYPE_APPLET);
                //加载参数
                exeLoadFile.setLoadParam(loadFile.getLoadParam());
                appletExeLoadFileMapper.add(exeLoadFile);
                i++;
                if (loadFile.getExeModuleList() != null && loadFile.getExeModuleList().size() > 0) {
                    for (ExeModule exeModule : loadFile.getExeModuleList()) {
                        AppletExeModuleForm moduleForm = new AppletExeModuleForm();
                        moduleForm.setId(StrUtil.newGuid());
                        moduleForm.setCreateUser(vo.getId());
                        moduleForm.setCreateDate(date);
                        moduleForm.setUpdateUser(vo.getId());
                        moduleForm.setUpdateDate(date);
                        moduleForm.setDelFlg(ResultCode.NOT_DEL);

                        moduleForm.setAid(exeModule.getAid());
                        moduleForm.setInstanceAid(exeModule.getInstanceAid());
                        moduleForm.setLoadFileId(exeLoadFile.getId());
                        appletExeModuleMapper.add(moduleForm);

                        if (exeModule.getInstanceList() != null && exeModule.getInstanceList().size() > 0) {
                            for (AppletInstance appletInstance : exeModule.getInstanceList()) {
                                AppletInstanceForm instanceForm = new AppletInstanceForm();
                                instanceForm.setId(StrUtil.newGuid());
                                instanceForm.setCreateUser(vo.getId());
                                instanceForm.setCreateDate(date);
                                instanceForm.setUpdateUser(vo.getId());
                                instanceForm.setUpdateDate(date);
                                instanceForm.setDelFlg(ResultCode.NOT_DEL);

                                instanceForm.setLoadFileId(exeLoadFile.getId());
                                instanceForm.setLoadFileAid(exeLoadFile.getAid());
                                instanceForm.setModuleId(moduleForm.getId());
                                instanceForm.setModuleAid(moduleForm.getAid());

                                instanceForm.setInstanceAid(appletInstance.getAid());
                                instanceForm.setInstallParam(appletInstance.getInstallParam());
                                appletExeModuleMapper.addInstance(instanceForm);
                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void appTest(String id) throws FrameworkRuntimeException {
        if (!checkCanTest()) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "提交测试应用个数过多");
        }

        AppletVo appletVo = appletMapper.findById(id);
        if (appletVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "找不到该应用");
        }
        if (StrUtil.isBlank(appletVo.getVersionId())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "请先上传Cap包");
        }
        if (!ConstantCode.APPLET_STATUS_NOT_COMMIT.equals(appletVo.getStatus())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "应用不是处于未提交状态，不能提交测试");
        }
        TestScriptVo testScriptVo = testScriptMapper.getActive();
        if (testScriptVo == null || StrUtil.isBlank(testScriptVo.getPath())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "没有激活通用脚本,不能测试");
        }

        //查找出是否有该COS版本的测试矩阵
        if (testMatrixMapper.isExist(appletVo.getVersionNo()) == 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "没有该COS版本的测试矩阵");
        }

        AppletForm appletForm = new AppletForm();
        appletForm.setId(appletVo.getId());
        appletForm.setStatus(ConstantCode.APPLET_STATUS_APPROVAL_PENDING);
        appletMapper.edit(appletForm);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void rejectTest(String id) throws FrameworkRuntimeException {

        AppletVo appletVo = appletMapper.findById(id);
        if (appletVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "找不到该应用");
        }

        AppletForm appletForm = new AppletForm();
        appletForm.setId(appletVo.getId());
        appletForm.setStatus(ConstantCode.APPLET_STATUS_TEST_REJECT);
        appletMapper.edit(appletForm);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FrameworkRuntimeException.class)
    public void commitTest(String id, Integer timeOut) throws FrameworkRuntimeException {
        /*条件限制：测试任务过多也不能提交*/
        if (!checkCanTest()) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "提交测试应用个数过多");
        }

        AppletVo appletVo = appletMapper.findById(id);
        if (appletVo == null) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "找不到该应用");
        }
        if (StrUtil.isBlank(appletVo.getVersionId())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "请先上传Cap包");
        }
        if (ConstantCode.APPLET_STATUS_WAITING_TEST.equals(appletVo.getStatus())
                || ConstantCode.APPLET_STATUS_TESTING.equals(appletVo.getStatus())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "状态是等待测试或者测试中的不能再提交测试");
        }
        TestScriptVo testScriptVo = testScriptMapper.getActive();
        if (testScriptVo == null || StrUtil.isBlank(testScriptVo.getPath())) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "没有激活通用脚本,不能测试");
        }

        //自定义脚本可以没有,有则需要检测
        String scriptId = null;
        if (StrUtil.isNotBlank(appletVo.getTestBusinessScriptId())) {
            TestBusinessScriptVo testBusinessScriptVo = testBusinessScriptMapper.findById(appletVo.getTestBusinessScriptId());
            if (testBusinessScriptVo == null) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "没有发现自定义脚本");
            }
            //状态是测试中的不能再提交
            if (ConstantCode.SCRIPT_STATUS_TESTING.equals(testBusinessScriptVo.getStatus())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "自定义脚本正在测试中,不能提交测试");
            }
            //自定义脚本预测试通过后才可以提交应用测试
            if (ConstantCode.SCRIPT_STATUS_TEST_FAIL.equals(testBusinessScriptVo.getStatus())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "自定义脚本测试失败,不能提交测试");
            }
            //自定义脚本没进行预测试不可以提交应用测试
            if (ConstantCode.SCRIPT_STATUS_NOT_COMMIT.equals(testBusinessScriptVo.getStatus())) {
                throw new FrameworkRuntimeException(ResultCode.Fail, "自定义脚本未进行预测试,不能提交测试");
            }
            scriptId = testBusinessScriptVo.getId();
        }

        //查找出激活的卡片组
        /*TestCardGroupForm testCardGroupForm = new TestCardGroupForm();
        testCardGroupForm.setStatus(TestCardGroupVo.ACTIVE_STATUS);
        List<TestCardGroupVo> list = testCardGroupMapper.findList(testCardGroupForm);
        if (list.size() == 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "请先设置激活卡片组");
        }
        if (list.size() > 1) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "数据错误:有多个激活的卡片组");
        }
        TestCardGroupRelateForm testCardGroupRelateForm = new TestCardGroupRelateForm();
        testCardGroupRelateForm.setCardGroupId(list.get(0).getId());
        List<TestCardGroupRelateVo> cardIds = testCardGroupRelateMapper.findList(testCardGroupRelateForm);
        if (cardIds.size() == 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "数据错误:激活的卡片组下没有关联卡片");
        }*/

        UserVo userVo = AuthCasClient.getUser();
        Date date = new Date();

        TestTaskForm testTaskForm = new TestTaskForm();
        testTaskForm.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
        testTaskForm.setId(StrUtil.newGuid());
        testTaskForm.setType(ConstantCode.TEST_TASK_TYPE_TEST);
        testTaskForm.setAppletId(appletVo.getId());
        testTaskForm.setAppletVersionId(appletVo.getVersionId());
        testTaskForm.setTestBusinessScriptId(scriptId);
        testTaskForm.setTestScriptId(testScriptVo.getId());
        testTaskForm.setTestCardGroupId("");
        testTaskForm.setCreateUser(userVo.getId());
        testTaskForm.setCreateDate(date);
        testTaskForm.setUpdateUser(userVo.getId());
        testTaskForm.setUpdateDate(date);
        testTaskForm.setDelFlg(ResultCode.NOT_DEL);
        testTaskForm.setTimeOut(timeOut);

        /*List<TestScheduleForm> forms = new ArrayList<>();
        for (TestCardGroupRelateVo data : cardIds) {
            TestScheduleForm testScheduleForm = new TestScheduleForm();
            testScheduleForm.setId(StrUtil.newGuid());
            testScheduleForm.setTestTaskId(testTaskForm.getId());
            testScheduleForm.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);
            testScheduleForm.setScheduleDate(date);
            testScheduleForm.setCreateUser(userVo.getId());
            testScheduleForm.setCreateDate(date);
            testScheduleForm.setUpdateUser(userVo.getId());
            testScheduleForm.setUpdateDate(date);
            if (StrUtil.isNotBlank(scriptId)) {
                //先不进行业务脚本的整体测试
                //testScheduleForm.setTestContent(ConstantCode.TEST_CONTENT_ALL);
                testScheduleForm.setTestContent(ConstantCode.TEST_CONTENT_GENERAL);
            } else {
                testScheduleForm.setTestContent(ConstantCode.TEST_CONTENT_GENERAL);
            }
            testScheduleForm.setDelFlg(ResultCode.NOT_DEL);
            testScheduleForm.setTestCardId(data.getCardId());
            forms.add(testScheduleForm);

            // 参数校验测试计划
            TestScheduleForm testParamScheduleForm = BeanUtils.copy(testScheduleForm, TestScheduleForm.class);
            assert testParamScheduleForm != null;
            testParamScheduleForm.setId(StrUtil.newGuid());
            testParamScheduleForm.setStatus(ConstantCode.APPLET_STATUS_NOT_COMMIT);
            testParamScheduleForm.setTestContent(ConstantCode.TEST_CONTENT_PARAM);
            forms.add(testParamScheduleForm);
        }*/

        //变更应用测试状态
        AppletForm appletForm = new AppletForm();
        appletForm.setId(appletVo.getId());
        appletForm.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);

        AppletVersionForm appletVersionForm = new AppletVersionForm();
        appletVersionForm.setId(appletVo.getVersionId());
        appletVersionForm.setStatus(ConstantCode.APPLET_STATUS_WAITING_TEST);

        appletMapper.edit(appletForm);
        appletVersionMapper.edit(appletVersionForm);
        testTaskMapper.add(testTaskForm);
        //testScheduleMapper.adds(forms);
    }

    private Boolean checkCanTest() {
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

    @Override
    public List<Map<String, Object>> appletCreateCount(AppletForm form) throws FrameworkRuntimeException {
        return appletMapper.appletCreateCount(form);
    }

    @Override
    public Map<String, Object> testDetail(AppletForm form) throws FrameworkRuntimeException {
        //获取应用详情先
        Map<String, Object> result = new HashMap<>(10);
        AppletVo vo = appletMapper.detail(form);
        result.put("applet", vo);

        List<TestReportVo> list = testReportMapper.listCommByTaskId(vo.getTaskId());
        if (list != null && list.size() > 0) {
            for (TestReportVo data : list) {
                if (StrUtil.isNotBlank(data.getLogPath())) {
                    data.setLogPath(AesUtil2.encryptData(data.getLogPath()));
                }
            }
            result.put("cardListInfo", list);
        }

        List<TestReportVo> paramChecklist = testReportMapper.listParamByTaskId(vo.getTaskId());
        if (paramChecklist != null && paramChecklist.size() > 0) {
            for (TestReportVo testReportVo : paramChecklist) {
                if (StrUtil.isNotBlank(testReportVo.getLogPath())) {
                    testReportVo.setLogPath(AesUtil2.encryptData(testReportVo.getLogPath()));
                }
            }
            result.put("paramChecklist", paramChecklist);
        }

        TestCheckReportVo testCheckReportVo = testCheckReportMapper.findByAppletIdAndVersionId(vo.getId(), vo.getVersionId());
        if (testCheckReportVo != null) {
            if (testCheckReportVo.getNonstandardApi() != null) {
                testCheckReportVo.setNonstandardApiList(JSONUtils.parseArray(testCheckReportVo.getNonstandardApi(), PackageInfo.class));
            }
            if (testCheckReportVo.getSensitiveApi() != null) {
                testCheckReportVo.setSensitiveApis(JSONUtils.parseArray(testCheckReportVo.getSensitiveApi(), String.class));
            }
            if (testCheckReportVo.getEventList() != null) {
                testCheckReportVo.setEventToolKit(JSONUtils.parseArray(testCheckReportVo.getEventList(), String.class));
            }
        } else {
            testCheckReportVo = new TestCheckReportVo();
            testCheckReportVo.setMemoryDtrSize(0);
            testCheckReportVo.setMemoryDtrSizeExpect(0);
            testCheckReportVo.setMemoryRtrSize(0);
            testCheckReportVo.setMemoryRtrSizeExpect(0);
            testCheckReportVo.setMemoryCodeSize(0);
            testCheckReportVo.setMemoryCodeSizeExpect(0);
            testCheckReportVo.setInstallNewAmount(0);
            testCheckReportVo.setInstallNewAmountExpect(0);
            testCheckReportVo.setInstallNewSpace(0);
            testCheckReportVo.setInstallNewSpaceExpect(0);
            testCheckReportVo.setInstallNewArrayAmount(0);
            testCheckReportVo.setInstallNewArrayAmountExpect(0);
            testCheckReportVo.setInstallNewArraySpace(0);
            testCheckReportVo.setInstallNewArraySpaceExpect(0);
            /***  performanceAnalysis  **/
            testCheckReportVo.setStaticReferenceArrayInitAmount(0);
            testCheckReportVo.setStaticReferenceArrayInitAmountExpect(0);
            testCheckReportVo.setStaticReferenceArrayInitSpace(0);
            testCheckReportVo.setStaticReferenceArrayInitSpaceExpect(0);
            testCheckReportVo.setStaticReferenceNullAmount(0);
            testCheckReportVo.setStaticReferenceNullAmountExpect(0);
            testCheckReportVo.setStaticReferenceNullSpace(0);
            testCheckReportVo.setStaticReferenceNullSpaceExpect(0);
            testCheckReportVo.setStaticPrimitiveDefaultAmount(0);
            testCheckReportVo.setStaticPrimitiveDefaultAmountExpect(0);
            testCheckReportVo.setStaticPrimitiveDefaultSpace(0);
            testCheckReportVo.setStaticPrimitiveDefaultSpaceExpect(0);
            testCheckReportVo.setStaticPrimitiveNonDefaultAmount(0);
            testCheckReportVo.setStaticPrimitiveNonDefaultAmountExpect(0);
            testCheckReportVo.setStaticPrimitiveNonDefaultSpace(0);
            testCheckReportVo.setStaticPrimitiveNonDefaultSpaceExpect(0);
            testCheckReportVo.setDownloadInstallTimeExpect(60);
            testCheckReportVo.setDownloadMaxTimeExpect(5);
            testCheckReportVo.setLoadC7MaxExpect(0);
            testCheckReportVo.setLoadC8MaxExpect(32768);
            testCheckReportVo.setInstallC7MaxExpect(1024);
            testCheckReportVo.setInstallC8MaxExpect(32768);
        }
        result.put("testCheckReportVo", testCheckReportVo);

        //安装参数
        List<TestReportDataVo> loadList = testReportDataMapper.maxLoadList(vo.getTaskId());
        if (loadList.size() > 0) {
            List<String> strList = new ArrayList<>();
            for (TestReportDataVo dataVo : loadList) {
//                strList.add(dataVo.getCapName() + ":" + GPUtil.genInstallForLoadParam(dataVo.getC6(), dataVo.getC7(), dataVo.getC8()));
                strList.add(dataVo.getCapName() + ": " + GPUtil.genInstallForLoadParam(dataVo.getC6(), dataVo.getC7(), dataVo.getC8()));
            }
            result.put("loadList", strList);
        }

        List<TestReportDataVo> installList = testReportDataMapper.maxInstallList(vo.getTaskId());
        if (installList.size() > 0) {
            List<String> newList = new ArrayList<>();
            for (TestReportDataVo dataVo : installList) {
//                newList.add(dataVo.getCapName() + ":" + TlvUtils.genInstallForInstallParam(dataVo.getC7(), dataVo.getC8(), dataVo.getInstallParam()));
                try {
                    newList.add(dataVo.getCapName() + ": " + GPUtil.genInstallForInstallParam(dataVo.getC7(), dataVo.getC8(), dataVo.getInstallParam()));
                } catch (TlvAnalysisException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            result.put("installList", newList);
        }

        //安全审核API
        ApiSaveRecordVo  apiSaveRecordVo = apiSaveRecordMapper.findByTaskId(vo.getTaskId());
        if(apiSaveRecordVo != null){
            result.put("disabledApis", apiSaveRecordVo.getApis().split(","));
        }
        return result;
    }

    @Override
    public List<TestReportApduVo> testApduDetail(TestReportApduForm form) throws FrameworkRuntimeException {
        return testReportApduMapper.list(form);
    }

    @Override
    public Pagination<AppletVo> examinePage(AppletForm form) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        if (ConstantCode.ROLE_DEVELOPER.equals(vo.getRoleId())) {
            form.setCreateUser(vo.getId());
        }
        if (form.getName() != null) {
            form.setName(form.getName().trim());
        }
        Page<Object> pageHelper = PageHelper.startPage(form.getPage(), form.getPageCount());
        form.setDelFlg(ResultCode.NOT_DEL);
        List<AppletVo> list = appletMapper.examineList(form);
        Pagination<AppletVo> pagination = new Pagination<>(form.getPage(), form.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public void pdfReport(HttpServletResponse res, String id) throws FrameworkRuntimeException, IOException {
//        Map<String, Object> result = new HashMap<>(10);
        AppletForm form = new AppletForm();
        form.setId(id);
        AppletVo vo = appletMapper.detail(form);
        AppletVo applet = findById(id);
        if (applet == null) {
            throw new FrameworkRuntimeException("找不到相对应的Applet。");
        }

        UserVo userVo = AuthCasClient.getUser();
        if (ConstantCode.ROLE_DEVELOPER.equals(userVo.getRoleId()) && !userVo.getId().equals(applet.getCreateUser())) {
            throw new FrameworkRuntimeException("不能越权下载。");
        }

        List<AppletExeLoadFileVo> loadFiles = applet.getLoadFiles();
        if (loadFiles == null && loadFiles.size() <= 0) {
            throw new FrameworkRuntimeException("应用包的 CAP 文件缺失。");
        }

        res.setContentType("application/octet-stream; charset=UTF-8");
        res.setHeader("Content-Disposition", "filename=" + URLEncoder.encode(vo.getName() + "-测试报告.pdf", StandardCharsets.UTF_8.name()));

        ServletOutputStream output = res.getOutputStream();
        BufferedOutputStream buff = new BufferedOutputStream(output);


        PdfUtil.TestReport testReport = new PdfUtil.TestReport();
        testReport.setTitle(vo.getName() + "-测试报告");

        List<PdfUtil.CapFile> capFileList = new ArrayList<>();
        for (AppletExeLoadFileVo lf : loadFiles) {
            PdfUtil.CapFile capFile = new PdfUtil.CapFile();
            capFile.setName(lf.getFileName());
            capFile.setHash(lf.getHash());
            capFileList.add(capFile);
        }
        testReport.setCapFileList(capFileList);


        List<TestReportVo> paramChecklist = testReportMapper.listParamByTaskId(vo.getTaskId());
        if(paramChecklist.size() == 0){
            //参数测试没做的话拿兼容性测试结果
            paramChecklist = testReportMapper.listCommByTaskId(vo.getTaskId());
        }

        int loadC7 = 0;
        int loadC8 = 0;
        int installC7 = 0;
        int installC8 = 0;

        if (paramChecklist != null && paramChecklist.size() > 0) {
            List<String> compatibilityList = new ArrayList<>();
            for (TestReportVo report : paramChecklist) {
                if (report.getLoadC7() != null && report.getLoadC7() > loadC7) {
                    loadC7 = report.getLoadC7();
                }

                if (report.getLoadC8() != null && report.getLoadC8() > loadC8) {
                    loadC8 = report.getLoadC8();
                }

                if (report.getC7() != null && report.getC7() > installC7) {
                    installC7 = report.getC7();
                }

                if (report.getC8() != null && report.getC8() > installC8) {
                    installC8 = report.getC8();
                }

                if (ConstantCode.RESULT_FAIL.equals(report.getResult())) {
                    compatibilityList.add(report.getManufacturerName() + " 测试失败");
                }else {
                    compatibilityList.add(report.getManufacturerName() + " 测试通过");
                }
            }
            testReport.setCardTestInfo(compatibilityList);
            testReport.setRam(String.valueOf(installC7));
            testReport.setNvm(String.valueOf(installC8));
        }

        //应用加载、安装预期总时间
        double diSumTime = testTaskMapper.diSumTime(vo.getTaskId());

        //应用下载单条指令最大时间
        double downloadMaxTime = testTaskMapper.downloadMaxTime(vo.getTaskId());

        testReport.setMaxPerTime(downloadMaxTime + "S");
        testReport.setTotalTime(diSumTime + "S");



        TestCheckReportVo testCheckReportVo = testCheckReportMapper.findByAppletIdAndVersionId(vo.getId(), vo.getVersionId());
        if (testCheckReportVo != null) {

//            if (testCheckReportVo.getNonstandardApi() != null) {
//                testCheckReportVo.setNonstandardApiList(JSONUtils.parseArray(testCheckReportVo.getNonstandardApi(), PackageInfo.class));
//            }
            if (testCheckReportVo.getSensitiveApi() != null) {
                testReport.setSensitiveApiList(JSONUtils.parseArray(testCheckReportVo.getSensitiveApi(), String.class));
//                testCheckReportVo.setSensitiveApis(JSONUtils.parseArray(testCheckReportVo.getSensitiveApi(), String.class));
            }
            if (testCheckReportVo.getEventList() != null) {
                testReport.setToolkitEventList(JSONUtils.parseArray(testCheckReportVo.getEventList(), String.class));
//                testCheckReportVo.setEventToolKit(JSONUtils.parseArray(testCheckReportVo.getEventList(), String.class));
            }
        }

        //安装参数
        List<TestReportDataVo> loadList = testReportDataMapper.maxLoadList(vo.getTaskId());
        if (loadList.size() > 0) {
            StringBuilder loadBuf = new StringBuilder();
            for (TestReportDataVo dataVo : loadList) {
               loadBuf.append(dataVo.getCapName()).append(": ").append(GPUtil.genInstallForLoadParam(dataVo.getC6(), dataVo.getC7(), dataVo.getC8())).append("\r\n");
            }
            testReport.setLoadParam(loadBuf.toString());
        }

        List<TestReportDataVo> installList = testReportDataMapper.maxInstallList(vo.getTaskId());
        if (installList.size() > 0) {
            StringBuilder installBuf = new StringBuilder();
            for (TestReportDataVo dataVo : installList) {
                try {
                    installBuf.append(dataVo.getCapName()).append(": ").append(GPUtil.genInstallForInstallParam(dataVo.getC7(), dataVo.getC8(), dataVo.getInstallParam())).append("\r\n");
                } catch (TlvAnalysisException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            testReport.setInstallParam(installBuf.toString());
        }

        try {
            if(ConstantCode.APPLET_STATUS_TEST_SUCCESS.equals(vo.getStatus())){
                testReport.setTestResult("通过");
            }else{
                testReport.setTestResult("未通过");
            }

            testReport.setTestTime(DateUtil.format(vo.getTestEnd()));
            PdfUtil.genTestReport(buff, testReport);
        } catch (PdfException e) {
            LOGGER.error("生成PDF报告出错！", e);
        }

    }

    @Override
    public void pdfReportSave(HttpServletResponse res, String id) throws FrameworkRuntimeException, IOException {
        AppletForm form = new AppletForm();
        form.setId(id);
        AppletVo vo = appletMapper.detail(form);
        AppletVo applet = findById(id);
        if (applet == null) {
            throw new FrameworkRuntimeException("找不到相对应的Applet。");
        }

        UserVo userVo = AuthCasClient.getUser();
        if (ConstantCode.ROLE_DEVELOPER.equals(userVo.getRoleId()) && !userVo.getId().equals(applet.getCreateUser())) {
            throw new FrameworkRuntimeException("不能越权下载。");
        }

        TemplateConfigVo templateConfigVo =  templateConfigMapper.findById(userVo.getTemplateId());
        if(templateConfigVo == null){
            throw new FrameworkRuntimeException("没有设置安全审核模板");
        }


        List<AppletExeLoadFileVo> loadFiles = applet.getLoadFiles();
        if (loadFiles == null && loadFiles.size() <= 0) {
            throw new FrameworkRuntimeException("应用包的 CAP 文件缺失。");
        }

        res.setContentType("application/octet-stream; charset=UTF-8");
        res.setHeader("Content-Disposition", "filename=" + URLEncoder.encode(vo.getName() + "-测试报告.pdf", StandardCharsets.UTF_8.name()));

        ServletOutputStream output = res.getOutputStream();
        BufferedOutputStream buff = new BufferedOutputStream(output);


        PdfUtil.TestReport testReport = new PdfUtil.TestReport();
        testReport.setTitle(vo.getName() + "-卡应用安全检测测试报告");

        List<PdfUtil.CapFile> capFileList = new ArrayList<>();
        for (AppletExeLoadFileVo lf : loadFiles) {
            PdfUtil.CapFile capFile = new PdfUtil.CapFile();
            capFile.setName(lf.getFileName());
            capFile.setHash(lf.getHash());
            capFileList.add(capFile);
        }
        testReport.setCapFileList(capFileList);


        List<TestReportVo> paramChecklist = testReportMapper.listParamByTaskId(vo.getTaskId());
        if(paramChecklist.size() == 0){
            //参数测试没做的话拿兼容性测试结果
            paramChecklist = testReportMapper.listCommByTaskId(vo.getTaskId());
        }

        int loadC7 = 0;
        int loadC8 = 0;
        int installC7 = 0;
        int installC8 = 0;

        if (paramChecklist != null && paramChecklist.size() > 0) {
            List<String> compatibilityList = new ArrayList<>();
            for (TestReportVo report : paramChecklist) {
                if (report.getLoadC7() != null && report.getLoadC7() > loadC7) {
                    loadC7 = report.getLoadC7();
                }

                if (report.getLoadC8() != null && report.getLoadC8() > loadC8) {
                    loadC8 = report.getLoadC8();
                }

                if (report.getC7() != null && report.getC7() > installC7) {
                    installC7 = report.getC7();
                }

                if (report.getC8() != null && report.getC8() > installC8) {
                    installC8 = report.getC8();
                }

                if (ConstantCode.RESULT_FAIL.equals(report.getResult())) {
                    compatibilityList.add(report.getManufacturerName() + " 测试失败");
                }else {
                    compatibilityList.add(report.getManufacturerName() + " 测试通过");
                }
            }
            testReport.setCardTestInfo(compatibilityList);
            testReport.setRam(String.valueOf(installC7));
            testReport.setNvm(String.valueOf(installC8));
        }


        TestCheckReportVo testCheckReportVo = testCheckReportMapper.findByAppletIdAndVersionId(vo.getId(), vo.getVersionId());
        if (testCheckReportVo != null) {
            if (testCheckReportVo.getSensitiveApi() != null) {
                testReport.setSensitiveApiList(JSONUtils.parseArray(testCheckReportVo.getSensitiveApi(), String.class));
            }
            if (testCheckReportVo.getEventList() != null) {
                testReport.setToolkitEventList(JSONUtils.parseArray(testCheckReportVo.getEventList(), String.class));
            }

            if (testCheckReportVo.getNonstandardApi() != null) {
                testReport.setNonstandardApiList(JSONUtils.parseArray(testCheckReportVo.getNonstandardApi(), PackageInfo.class));
            }
        }
        //禁用API需要列出来
        ApiSaveRecordVo  apiSaveRecordVo = apiSaveRecordMapper.findByTaskId(vo.getTaskId());
        if(apiSaveRecordVo != null){
            testReport.setDenyApiList(Arrays.asList(apiSaveRecordVo.getApis().split(",")));
        }else{
            testReport.setDenyApiList(new ArrayList<>());
        }

        try {
            if(ConstantCode.APPLET_STATUS_TEST_SUCCESS.equals(vo.getStatus())){
                testReport.setTestResult("通过");
            }else{
                testReport.setTestResult("未通过");
            }

            if(testReport.getDenyApiList().size() > 0){
                testReport.setTestResult(testReport.getTestResult() + " [存在禁止/限制调用情况，需向管理方报备用途，获得同意后方可使用]");
            }

            testReport.setTestTime(DateUtil.format(vo.getTestEnd()));
            PdfUtil.genTestReport(buff, testReport, templateConfigVo);
        } catch (PdfException e) {
            LOGGER.error("生成PDF报告出错！", e);
        }
    }

    @Override
    public Map<String, Object> testDetailApi(AppletForm form) throws FrameworkRuntimeException, IOException {
        //获取应用详情先
        Map<String, Object> result = new HashMap<>(10);
        AppletVo vo = appletMapper.detail(form);
        if (vo == null) {
            throw new FrameworkRuntimeException(ThirdCode.APP_ID_DOES_NOT_EXIST, "应用ID不存在");
        }

        result.put("result", vo.getStatus());
        if (ConstantCode.APPLET_STATUS_TESTING.equals(vo.getStatus()) ||
                ConstantCode.APPLET_STATUS_WAITING_TEST.equals(vo.getStatus())) {
            return result;
        }

        String realPath = reportPath + form.getId() + File.separator;
        String fileName = vo.getName() + ".xls";
        String tempFilename = File.separator + fileName;
        tempFilename = realPath + tempFilename;
        File reportFile = new File(tempFilename);
        if (reportFile.exists()) {
            result.put("downloadUrl", downloadUrl + AesUtil2.encryptData(tempFilename));
            return result;
        }

        String path = excelPath + File.separator + "report.xls";
        File file = ResourceUtils.getFile(path);
        FileInputStream fileInStream = new FileInputStream(file);
        POIFSFileSystem fs = new POIFSFileSystem(fileInStream);
        fileInStream.close();
        //读取excel模板
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet detail = wb.getSheetAt(0);
        detail.setForceFormulaRecalculation(true);
        detail.getRow(0).getCell(0).setCellValue("应用名称：" + vo.getName() + "\n测试日期：" + DateUtil.formatsSort(new Date()));

        File baseDir = new File(realPath);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        //虚拟cos数据分析
        TestCheckReportVo testCheckReportVo = testCheckReportMapper.findByAppletIdAndVersionId(vo.getId(), vo.getVersionId());
        if (testCheckReportVo != null) {
            dataAnalysis(vo.getTaskId(), testCheckReportVo, result, detail);
        }

        //实卡测试分析
        List<TestReportVo> list = testReportMapper.listCommByTaskId(vo.getTaskId());
        if (list != null && list.size() > 0) {
            List<String> compatibilityList = new ArrayList<>();
            boolean compatibilityFlag = false;
            String compatibilityMsg = "";
            for (TestReportVo data : list) {
                if (ConstantCode.RESULT_FAIL.equals(data.getResult())) {
                    compatibilityFlag = true;
                    compatibilityMsg += data.getManufacturerName() + " 测试失败 " + "\n";
                    compatibilityList.add(data.getManufacturerName() + " 测试失败");
                }
            }
            //result.put("compatibility", compatibilityList);
            if (compatibilityFlag) {
                detail.getRow(21).getCell(3).setCellValue("不通过");
                detail.getRow(21).getCell(4).setCellValue(compatibilityMsg);
            } else {
                detail.getRow(21).getCell(3).setCellValue("通过");
            }
        }

        List<TestReportVo> paramChecklist = testReportMapper.listParamByTaskId(vo.getTaskId());
        if (paramChecklist != null && paramChecklist.size() > 0) {
            List<String> paramList = new ArrayList<>();
            boolean paramCheckFlag = false;
            String paramCheckMsg = "";
            for (TestReportVo testReportVo : paramChecklist) {
                if (ConstantCode.RESULT_FAIL.equals(testReportVo.getResult())) {
                    paramCheckFlag = true;
                    paramCheckMsg += testReportVo.getManufacturerName() + " 测试失败 " + "\n";
                    paramList.add(testReportVo.getManufacturerName() + " 测试失败");
                }
            }
            //result.put("paramCheck", paramList);
            if (paramCheckFlag) {
                detail.getRow(22).getCell(3).setCellValue("不通过");
                detail.getRow(22).getCell(4).setCellValue(paramCheckMsg);
            } else {
                detail.getRow(22).getCell(3).setCellValue("通过");
            }
        } /*else {
            result.put("paramCheck", new ArrayList<>());
        }*/

        //安装参数
        String param = "";
        List<TestReportDataVo> loadList = testReportDataMapper.maxLoadList(vo.getTaskId());
        if (loadList.size() > 0) {
            param += "InstallForLoad：\n";
            for (TestReportDataVo dataVo : loadList) {
                param += dataVo.getCapName() + ": " + GPUtil.genInstallForLoadParam(dataVo.getC6(), dataVo.getC7(), dataVo.getC8()) + "\n";
            }
        }

        List<TestReportDataVo> installList = testReportDataMapper.maxInstallList(vo.getTaskId());
        if (installList.size() > 0) {
            param += "\nInstallForInstall参数：\n";
            for (TestReportDataVo dataVo : installList) {
                try {
                    param += dataVo.getCapName() + ": " + GPUtil.genInstallForInstallParam(dataVo.getC7(), dataVo.getC8(), dataVo.getInstallParam()) + "\n";
                } catch (TlvAnalysisException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        detail.getRow(23).getCell(4).setCellValue(param);

        FileOutputStream out = new FileOutputStream(tempFilename);
        wb.write(out);
        out.close();


        result.put("downloadUrl", downloadUrl + AesUtil2.encryptData(tempFilename));
        return result;
    }

    private void dataAnalysis(String testTaskId, TestCheckReportVo testCheckReportVo,
                              Map<String, Object> result, HSSFSheet detail) {
        /*if (testCheckReportVo.getNonstandardApi() != null) {
            result.put("non-standard-api", JSONUtils.parseArray(testCheckReportVo.getNonstandardApi(), PackageInfo.class));
        }*/
        if (testCheckReportVo.getSensitiveApi() != null) {
            List<String> sensitiveApis = JSONUtils.parseArray(testCheckReportVo.getSensitiveApi(), String.class);
            //result.put("sensitive-api", sensitiveApis);
            if (sensitiveApis.size() > 0) {
                String sensitiveApiString = "";
                for (String s : sensitiveApis) {
                    sensitiveApiString += s + "\n";
                }
                detail.getRow(2).getCell(3).setCellValue("不通过");
                detail.getRow(2).getCell(4).setCellValue(sensitiveApiString);
            } else {
                detail.getRow(2).getCell(3).setCellValue("通过");
            }
        } else {
            detail.getRow(2).getCell(3).setCellValue("通过");
        }

        if (testCheckReportVo.getEventList() != null) {
            List<String> eventToolKit = JSONUtils.parseArray(testCheckReportVo.getEventList(), String.class);
            //result.put("toolkit-event", eventToolKit);
            if (eventToolKit.size() > 0) {
                String eventToolKitString = "";
                for (String s : eventToolKit) {
                    eventToolKitString += s + "\n";
                }
                detail.getRow(3).getCell(3).setCellValue("不通过");
                detail.getRow(3).getCell(4).setCellValue(eventToolKitString);
            } else {
                detail.getRow(3).getCell(3).setCellValue("通过");
            }
        } else {
            detail.getRow(3).getCell(3).setCellValue("通过");
        }

        List<String> analysis = new ArrayList<>();
        if (testCheckReportVo.getMemoryDtrSize() > testCheckReportVo.getMemoryDtrSizeExpect()) {
            analysis.add("dtr实际值（" + testCheckReportVo.getMemoryDtrSize() + "）比期望值（" + testCheckReportVo.getMemoryDtrSizeExpect() + "）大");
            detail.getRow(4).getCell(3).setCellValue("不通过");
            //detail.getRow(4).getCell(4).setCellValue("dtr实际值（" + testCheckReportVo.getMemoryDtrSize() + "）比期望值（" + testCheckReportVo.getMemoryDtrSizeExpect() + "）大");
        } else {
            detail.getRow(4).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getMemoryRtrSize() > testCheckReportVo.getMemoryRtrSizeExpect()) {
            analysis.add("rtr实际值（" + testCheckReportVo.getMemoryRtrSize() + "）比期望值（" + testCheckReportVo.getMemoryRtrSizeExpect() + "）大");
            detail.getRow(5).getCell(3).setCellValue("不通过");
            //detail.getRow(5).getCell(4).setCellValue("rtr实际值（" + testCheckReportVo.getMemoryRtrSize() + "）比期望值（" + testCheckReportVo.getMemoryRtrSizeExpect() + "）大");
        } else {
            detail.getRow(5).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getMemoryCodeSize() > testCheckReportVo.getMemoryCodeSizeExpect()) {
            analysis.add("代码空间大小实际值（" + testCheckReportVo.getMemoryCodeSize() + "）比期望值（" + testCheckReportVo.getMemoryCodeSizeExpect() + "）大");
            detail.getRow(6).getCell(3).setCellValue("不通过");
            //detail.getRow(6).getCell(4).setCellValue("代码空间大小实际值（" + testCheckReportVo.getMemoryCodeSize() + "）比期望值（" + testCheckReportVo.getMemoryCodeSizeExpect() + "）大");
        } else {
            detail.getRow(6).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getInstallNewAmount() > testCheckReportVo.getInstallNewAmountExpect()) {
            analysis.add("new对象数量实际值（" + testCheckReportVo.getInstallNewAmount() + "）比期望值（" + testCheckReportVo.getInstallNewAmountExpect() + "）大");
            detail.getRow(7).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(7).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getInstallNewSpace() > testCheckReportVo.getInstallNewSpaceExpect()) {
            analysis.add("new对象空间实际值（" + testCheckReportVo.getInstallNewSpace() + "）比期望值（" + testCheckReportVo.getInstallNewSpaceExpect() + "）大");
            detail.getRow(8).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(8).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getInstallNewArrayAmount() > testCheckReportVo.getInstallNewArrayAmountExpect()) {
            analysis.add("new数组数量实际值（" + testCheckReportVo.getInstallNewArrayAmount() + "）比期望值（" + testCheckReportVo.getInstallNewArrayAmountExpect() + "）大");
            detail.getRow(9).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(9).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getInstallNewArraySpace() > testCheckReportVo.getInstallNewArraySpaceExpect()) {
            analysis.add("new数组空间实际值（" + testCheckReportVo.getInstallNewArraySpace() + "）比期望值（" + testCheckReportVo.getInstallNewArraySpaceExpect() + "）大");
            detail.getRow(10).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(10).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getStaticReferenceArrayInitAmount() > testCheckReportVo.getStaticReferenceArrayInitAmountExpect()) {
            analysis.add("引用类型->基本类型数组初始化数量（" + testCheckReportVo.getStaticReferenceArrayInitAmount() + "）比期望值（" + testCheckReportVo.getStaticReferenceArrayInitAmountExpect() + "）大");
            detail.getRow(17).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(17).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getStaticReferenceArrayInitSpace() > testCheckReportVo.getStaticReferenceArrayInitSpaceExpect()) {
            analysis.add("引用类型->基本类型数组初始化空间（" + testCheckReportVo.getStaticReferenceArrayInitSpace() + "）比期望值（" + testCheckReportVo.getStaticReferenceArrayInitSpaceExpect() + "）大");
            detail.getRow(18).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(18).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getStaticReferenceNullAmount() > testCheckReportVo.getStaticReferenceNullAmountExpect()) {
            analysis.add("null值静态引用类型的变量实际数量（" + testCheckReportVo.getStaticReferenceNullAmount() + "）比期望值（" + testCheckReportVo.getStaticReferenceNullAmountExpect() + "）大");
            detail.getRow(15).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(15).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getStaticReferenceNullSpace() > testCheckReportVo.getStaticReferenceNullSpaceExpect()) {
            analysis.add("null值静态引用类型的变量实际空间（" + testCheckReportVo.getStaticReferenceNullSpace() + "）比期望值（" + testCheckReportVo.getStaticReferenceNullSpaceExpect() + "）大");
            detail.getRow(16).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(16).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getStaticPrimitiveDefaultAmount() > testCheckReportVo.getStaticPrimitiveDefaultAmountExpect()) {
            analysis.add("初始化为缺省值的变量实际数量（" + testCheckReportVo.getStaticPrimitiveDefaultAmount() + "）比期望值（" + testCheckReportVo.getStaticPrimitiveDefaultAmountExpect() + "）大");
            detail.getRow(11).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(11).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getStaticPrimitiveDefaultSpace() > testCheckReportVo.getStaticPrimitiveDefaultSpaceExpect()) {
            analysis.add("初始化为缺省值的变量实际空间（" + testCheckReportVo.getStaticPrimitiveDefaultSpace() + "）比期望值（" + testCheckReportVo.getStaticPrimitiveDefaultSpaceExpect() + "）大");
            detail.getRow(12).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(12).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getStaticPrimitiveNonDefaultAmount() > testCheckReportVo.getStaticPrimitiveNonDefaultAmountExpect()) {
            analysis.add("初始化为非缺省值的变量实际数量（" + testCheckReportVo.getStaticPrimitiveNonDefaultAmount() + "）比期望值（" + testCheckReportVo.getStaticPrimitiveNonDefaultAmountExpect() + "）大");
            detail.getRow(13).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(13).getCell(3).setCellValue("通过");
        }
        if (testCheckReportVo.getStaticPrimitiveNonDefaultSpace() > testCheckReportVo.getStaticPrimitiveNonDefaultSpaceExpect()) {
            analysis.add("初始化为非缺省值的变量实际空间（" + testCheckReportVo.getStaticPrimitiveNonDefaultSpace() + "）比期望值（" + testCheckReportVo.getStaticPrimitiveNonDefaultSpaceExpect() + "）大");
            detail.getRow(14).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(14).getCell(3).setCellValue("通过");
        }

        //应用加载、安装预期总时间
        double diSumTime = testTaskMapper.diSumTime(testTaskId);
        if (diSumTime > testCheckReportVo.getDownloadInstallTimeExpect()) {
            detail.getRow(20).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(20).getCell(3).setCellValue("通过");
        }

        //应用下载单条指令最大时间
        double downloadMaxTime = testTaskMapper.downloadMaxTime(testTaskId);
        if (downloadMaxTime > testCheckReportVo.getDownloadMaxTimeExpect()) {
            detail.getRow(19).getCell(3).setCellValue("不通过");
        } else {
            detail.getRow(19).getCell(3).setCellValue("通过");
        }

        //result.put("dataAnalysis", analysis);
    }
}
