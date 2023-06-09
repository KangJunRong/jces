package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class VmCosFileForm extends BaseForm {
    private String id;
    private String fileId;
    private Integer no;
    private String cosId;
    private String fileName;
    private String size;
    private Short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;
    private String fileHash;

    /*大版本号*/
    private String versionNo;
}
