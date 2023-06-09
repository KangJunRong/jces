package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppletInstanceVo {
    private String id;
    private String loadFileId;
    private String moduleId;
    private String loadFileAid;
    private String moduleAid;
    private String instanceAid;
    private String installParam;
    private Short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;

    private String instanceId;

}