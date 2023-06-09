package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class VmCosDownloadForm extends BaseForm{
    private String id;
    private String cosVersion;
    private Integer cosNo;
    private String ip;
    private Integer downloadType;
    private Date downloadTime;

    /*查询的起始时间与终止时间*/
    private Date startTime;
    private Date endTime;
}
