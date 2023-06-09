package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppletVersionVo extends BaseVo{
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
    private String examine;
    private String reason;
}