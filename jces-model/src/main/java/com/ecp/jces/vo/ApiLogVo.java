package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiLogVo {
    private String id;
    private String name;
    private String uri;
    private String result;
    private String ip;
    private String appletVersionId;
    private short delFlg;
    private long responseTime;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;
}
