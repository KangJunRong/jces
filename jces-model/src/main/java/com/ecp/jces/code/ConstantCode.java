package com.ecp.jces.code;

import java.io.File;

/**
 * @program: jces
 * @description: 常量定义
 * @author: KJR
 * @create: 2021-04-09 09:33
 **/
public class ConstantCode {
    /**
     * test_report result 1=成功，2=失败
     */
    public static final Short TEST_REPORT_RESULT_SUCCESS = 1;
    public static final Short TEST_REPORT_RESULT_FAIL = 2;

    /**
     * 提交测试url
     */
    public static final String COS_VERSION_LABEL = "cos_version";

    /**
     * 提交测试url
     */
    public static final String COMMIT_TEST_URI = "commitTest";

    /**
     * 默认安装参数
     */
    public static final String C900 = "C900";

    /**
     * Execution输出日志路径
     */
    public static final String LOG_SUFFIX_ZIP = "zip";

    /**
     * Execution输出日志路径
     */
    public static final String LOG = "log" + File.separator;

    /**
     * 应用公共脚本存放cap包路径
     */
    public static final String COMMON_CAP_PATH = "TestApplet";

    /**
     * CAP包加载顺序路径
     */
    public static final String CAP_JS_PATH = "CapInfo.js";

    /**
     * 系统用户ID
     */
    public static final String SYSTEM_ADMIN_ID = "1";

    /**
     * 停用
     */
    public static final String USER_STATUS_DISABLED = "1";
    /**
     * 启用
     */
    public static final String USER_STATUS_AVAILABLE = "0";

    /**
     * 状态,0=未提交,1=待测试,2=测试中,3=测试成功,4=测试失败
     * <p>
     * 新状态：0=未提交，
     */
    public static final String APPLET_STATUS_NOT_COMMIT = "0";
    public static final String APPLET_STATUS_APPROVAL_PENDING = "10";
    public static final String APPLET_STATUS_WAITING_TEST = "1";
    public static final String APPLET_STATUS_TESTING = "2";
    public static final String APPLET_STATUS_TEST_SUCCESS = "3";
    //    public static final String APPLET_STATUS_VERIFY_SUCCESS = "31";
    public static final String APPLET_STATUS_TEST_FAIL = "4";
    public static final String APPLET_STATUS_TEST_REJECT = "50";

    /**
     * 状态,0=未测试,1=测试中,2=测试成功,3=测试失败,4=已发布
     */
    public static final String SCRIPT_STATUS_NOT_COMMIT = "0";
    public static final String SCRIPT_STATUS_TESTING = "1";
    public static final String SCRIPT_STATUS_TEST_SUCCESS = "2";
    public static final String SCRIPT_STATUS_TEST_FAIL = "3";
    public static final String SCRIPT_STATUS_RELEASE = "4";
    /**
     * 1管理员 2开发者
     */
    public static final String ROLE_ADMINISTRATORS = "1";
    public static final String ROLE_DEVELOPER = "2";

    /**
     * 测试任务类型,1=测试，2=预测试， 3=参数校验
     */
    public static final Integer TEST_TASK_TYPE_TEST = 1;
    public static final Integer TEST_TASK_TYPE_PRETEST = 2;
    public static final Integer TEST_TASK_TYPE_VERIFY = 3; // 参数校验

    /**
     * Execution Task Result,0=成功，1=失败
     */
    public static final String EXECUTION_TASK_SUCCESS = "0";
    public static final String EXECUTION_TASK_FAIL = "1";

    /**
     * 1=通用脚本测试，2=业务脚本测试，3=两者都测， 4=参数校验
     */
    public static final Integer TEST_CONTENT_GENERAL = 1;
    public static final Integer TEST_CONTENT_BUSINESS = 2;
    public static final Integer TEST_CONTENT_ALL = 3;
    public static final Integer TEST_CONTENT_PARAM = 4;

    public static final String TEST_CONTENT_GENERAL_MSG = "公共测试";
    public static final String TEST_CONTENT_BUSINESS_MSG = "业务测试";
    public static final String TEST_CONTENT_PARAM_MSG = "参数校验测试";

    /**
     * 包类型，0=lib，1=applet
     */
    public static final Integer LOAD_FILE_TYPE_LIB = 0;
    public static final Integer LOAD_FILE_TYPE_APPLET = 1;

    /**
     * Execution传递参数常量
     */
    public static final String EXECUTION_PARAM_TESTTASK_ID = "testTaskId";
    public static final String EXECUTION_PARAM_TESTSCHEDULE_ID = "testScheduleId";
    public static final String EXECUTION_PARAM_LOADDATA = "loadData";
    public static final String EXECUTION_PARAM_INSTALLDATA = "installData";
    public static final String EXECUTION_PARAM_RATE = "rate";
    public static final String EXECUTION_PARAM_DOWNLOAD = "download";
    public static final String EXECUTION_PARAM_INSTALL = "install";
    public static final String EXECUTION_PARAM_UNLOAD = "unload";

    /**
     * 感敏API
     */
    public static final String SENSITIVE_API_RTR = "javacard.framework.JCSystem.makeTransientByteArray(RTR)";

    /**
     * 审核状态,0=未提交,1=通过,2=拒绝,3=待审核
     */
    public static final String EXAMINE_STATUS_NOT_COMMIT = "0";
    public static final String EXAMINE_STATUS_WAITING_TEST = "3";
    public static final String EXAMINE_STATUS_TEST_SUCCESS = "1";
    public static final String EXAMINE_STATUS_TEST_FAIL = "2";

    /**
     * 字典类型,test_param
     */
    public static final String SYS_CONF_TEXT_PARAM = "test_param";

    /**
     * test_report 公共/业务测试结果 1成功 2失败
     */
    public static final Short RESULT_SUCCESS = 1;
    public static final Short RESULT_FAIL = 2;

    /*
     * vm_cos 文件状态 1保存 2发布 3归档
     */
    public static final Integer STATUS_SAVE = 1;
    public static final Integer STATUS_PUBLISH = 2;
    public static final Integer STATUS_PIGEONHOLE = 3;

    /**
     * vm_download_log cos下载类型 1测试引擎 2ide
     */
    public static final Integer COS_DOWNLOAD_ENGINE = 1;
    public static final Integer COS_DOWNLOAD_IDE = 2;

    public static final String VM_COS_TEST_FLAG = "jces:vm:test:flag:";
    /**
     * 公共测试已下发标记
     */
    public static final String TASK_START = "jces:task:start:";
    /**
     * 队列主题
     */
    public static final String EXCHANGE = "ENGINE_MSG";
    /**
     * IP白名单缓存key
     */
    public static final String WHITELIST = "jces:whitelist";

    /**
     * 更新矩阵信息标识key
     */
    public static final String UPDATE_ENGINE_INFO = "jces:update_engine_info:";

    /**
     * 安全审核标识 nvm,disabled_api,non_standard_api,sensitive_api,toolkit
     */
    public static final String MVN_FLAG = "nvm";
    public static final String DISABLED_API_FLAG = "disabled_api";
    public static final String NON_STANDARD_API_FLAG = "non_standard_api";
    public static final String SENSITIVE_API_FLAG = "sensitive_api";
    public static final String TOOLKIT_FLAG = "toolkit";
}
