package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
public class AppletExeLoadFileForm {
    private String id;
    private String appletVersionId;
    private String appletId;
    private Integer type;
    private String name;
    private String aid;
    private String fileName;
    private Integer loadSequence;
    private String loadParam;
    private String hash;
    private Short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;
}