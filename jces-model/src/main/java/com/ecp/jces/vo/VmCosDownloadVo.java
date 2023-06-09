package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class VmCosDownloadVo {
    private String id;
    private String cosVersion;
    private Integer cosNo;
    private String ip;
    private Integer downloadType;
    private Date downloadTime;
}
