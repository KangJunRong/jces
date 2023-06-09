package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppletExeModuleVo {
    private String id;
    private String loadFileId;
    private String name;
    private String aid;
    private String instanceAid;
    private Short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;

    private String moduleAid;
    private List<AppletInstanceVo> instanceVoList;
}