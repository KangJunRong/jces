package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiRoleVo {
    private String id;
    private String name;
    private String remark;
    private short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;
}
