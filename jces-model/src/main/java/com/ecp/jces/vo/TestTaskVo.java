package com.ecp.jces.vo;

import com.ecp.jces.form.BaseForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestTaskVo extends BaseForm{
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

    private String versionNo;

    private Integer rate;

    private String matrixId;

    private Boolean paramTest;
}