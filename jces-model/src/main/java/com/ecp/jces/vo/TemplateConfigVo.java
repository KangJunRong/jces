package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TemplateConfigVo extends BaseVo {
    private String id;

    private String collects;

    private String name;

    private Short delFlg;

    private String updateUser;

    private Date updateDate;

    private String createUser;

    private Date createDate;

    private String remark;
}