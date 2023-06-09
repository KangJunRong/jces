package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserForm extends BaseForm{

    private String id;

    private String name;

    private String account;

    private String password;

    private String email;

    private String description;

    private String roleId;

    private String status;

    private Short delFlg;

    private String updateUser;

    private Date updateDate;

    private String createUser;

    private Date createDate;

    private Date createDateStart;

    private Date createDateEnd;

    private Integer licenceCodeQuota; //授权码限额

    private String oldPassword;

    private String uuid;
    /**验证码**/
    private String code;

    /**论坛密码**/
    private String forumPassword;

    /**IP**/
    private String ip;

    /**第三方接入凭证**/
    private String voucher;

    /**电话**/
    private String phone;

    /**设置审核报告模板**/
    private String templateId;
}