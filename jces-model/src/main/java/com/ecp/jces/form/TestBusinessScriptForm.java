package com.ecp.jces.form;

import com.ecp.jces.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestBusinessScriptForm extends BaseForm {
    private String id;
    private String name;
    private String appletId;
    private Integer version;
    private String path;
    private String logPath;
    private String description;
    private String status;
    private Short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;
    private String errorInfo;

    private Date testStart;
    private Date testEnd;

    private String appletName;
    private String appletAid;
    private String packageAid;
}