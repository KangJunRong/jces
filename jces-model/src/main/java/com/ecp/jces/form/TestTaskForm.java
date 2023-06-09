package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
public class TestTaskForm extends BaseForm{
    private String exMsg;
    private String engineId;

    private String id;
    private Integer type;
    private String appletId;

    private String appletVersionId;

    private String testScriptId;

    private String testBusinessScriptId;

    private String testCardGroupId;

    private Date testStart;

    private Date testEnd;

    private String status;

    private Short delFlg;

    private String updateUser;

    private Date updateDate;

    private String createUser;

    private Date createDate;
    private Date createDateStart;
    private Date createDateEnd;

    private Date updateDateStart;
    private Date updateDateEnd;

    private Integer rate;
    private String matrixId;
    private Boolean paramTest;
    /**冗余 测试超时时间**/
    private Integer timeOut;
}