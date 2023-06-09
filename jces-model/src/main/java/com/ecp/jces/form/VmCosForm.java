package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class VmCosForm extends BaseForm {

    private String id;

    private String versionNo;

    private Integer status;

    private String remark;

    private String updateUser;

    private Date updateDate;

    private String createUser;

    private Date createDate;

    private Short delFlg;

    private VmCosFileForm fileForm;
}
