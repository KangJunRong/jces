package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
public class TestScheduleForm  extends BaseForm{
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
}