package com.ecp.jces.vo;

import com.ecp.jces.form.BaseForm;
import com.ecp.jces.vo.extra.AppletLoadFileVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppletVo extends BaseVo{
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

    /**冗余字段**/
    private Date testStart;
    private Date testEnd;
    private List<AppletExeLoadFileVo> loadFiles;

    private Integer commonVersion;
    private Integer customizeVersion;
    private String taskId;
    private Integer rate;

    private String examine;
    private String reason;
    private String versionNo;
}