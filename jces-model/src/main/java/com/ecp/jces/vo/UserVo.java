package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @program: cas
 * @description:
 * @author: KJR
 * @create: 2020-09-24 12:19
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class UserVo {

    private String id;

    private String name;

    private String account;

    private String email;

    private String description;

    private String roleId;

    private String status;

    private Short delFlg;

    private String updateUser;

    private Date updateDate;

    private String createUser;

    private Date createDate;

    private String password;

    private RoleVo role;

    /**论坛密码**/
    private String forumPassword;

    private Integer licenceCodeQuota; //授权码限额

    private Integer applieLicenceCodedNumber; //已申请授权码数量

    /**第三方接入凭证**/
    private String voucher;

    /**电话**/
    private String phone;

    /**设置审核报告模板**/
    private String templateId;
}
