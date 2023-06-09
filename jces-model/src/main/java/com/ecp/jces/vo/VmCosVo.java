package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class VmCosVo {

    private String id;

    private String versionNo;

    private Integer status;

    private String remark;

    private String updateUser;

    private Date updateDate;

    private String createUser;

    private Date createDate;

    private short delFlg;
}
