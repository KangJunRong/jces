package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestScheduleVo extends BaseVo{
    private String id;

    private String testTaskId;

    private String testEngineId;

    private String testCardId;

    private String testEngineReaderId;

    private Date scheduleDate;

    private Date testStart;

    private Date testEnd;

    private String status;

    private Short delFlg;

    private String updateUser;

    private Date updateDate;

    private String createUser;

    private Date createDate;

    /**1=通用脚本测试，2=业务脚本测试，3=两者都测 **/
    private Integer testContent;

    /**冗余 当前测试脚本类型 1=通用脚本测试，2=业务脚本测试**/
    private Integer currentType;

    /**冗余 测试日志的下载路径 **/
    private String businessLogPath;

    /**冗余 业务测试的错误信息 **/
    private String errorInfo;

}