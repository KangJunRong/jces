package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
public class AppletVersionForm extends BaseForm{
    private String id;
    private String name;
    private Integer version;
    private String appletId;
    private String capPath;
    private String description;
    private String status;
    private Short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;
    private Date createDateStart;
    private Date createDateEnd;
    private String examine;
    private String reason;
}