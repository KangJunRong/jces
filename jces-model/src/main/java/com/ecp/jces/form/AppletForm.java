package com.ecp.jces.form;

import com.ecp.jces.vo.extra.CapUploadVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppletForm extends BaseForm{
    private String id;
    private String name;
    private String packageAid;
    private String appletAid;
    private String versionId;
    private Integer lastVersion;
    private String testBusinessScriptId;
    private Integer businessScriptLastVersion;
    private String instanceAid;
    private String installParam;
    private String loadParam;
    private String description;
    private String status;
    private Short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;

    /**冗余 cap包路径**/
    private CapUploadVo capPath;
    /**冗余 cap包描述**/
    private String capDesc;

    private Date createDateStart;

    private Date createDateEnd;
    private String examine;
    private String reason;
    private String versionNo;

    /**冗余 测试超时时间**/
    private Integer timeOut;
}