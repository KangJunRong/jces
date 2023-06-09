package com.ecp.jces.form;


import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
public class TemplateConfigForm extends BaseForm {
    private String id;

    private String collects;

    private String remark;

    private String name;

    private Short delFlg;

    private String updateUser;

    private Date updateDate;

    private String createUser;

    private Date createDate;

}