package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppletExeLoadFileVo {
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

    private List<AppletExeModuleVo> moduleVoList;
    private String path;
}