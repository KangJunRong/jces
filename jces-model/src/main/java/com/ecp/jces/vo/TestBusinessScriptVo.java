package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestBusinessScriptVo extends BaseVo {
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
    private String appletName;
    private String appletAid;

    private Date testStart;
    private Date testEnd;

}