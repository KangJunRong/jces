package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiLogForm extends BaseForm {
    private String id;
    private String name;
    private String uri;
    private String result;
    private String ip;
    private String appletVersionId;
    private Short delFlg;
    private long responseTime;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;

    private Date startTime;
    private Date endTime;
}
