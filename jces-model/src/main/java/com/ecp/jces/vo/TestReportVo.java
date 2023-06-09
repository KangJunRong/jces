package com.ecp.jces.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestReportVo extends BaseVo{
    private String id;
    private String appletId;
    private String appletVersionId;
    private String testScheduleId;
    private String testCardId;
    private Short result;
    private String logPath;
    private Short businessResult;
    private String businessLogPath;
    private Short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;
    private String errorInfo;
    private Integer c6;
    private Integer c7;
    private Integer c8;
    private Integer loadC6;
    private Integer loadC7;
    private Integer loadC8;
    private BigDecimal downloadAllTime;
    private BigDecimal downloadMaxTime;
    private BigDecimal installAllTime;
    private BigDecimal installMaxTime;
    private BigDecimal unloadAllTime;
    private BigDecimal unloadMaxTime;
    private BigDecimal downloadMinTime;
    private BigDecimal installMinTime;
    private BigDecimal unloadMinTime;

    /**冗余**/
    private String manufacturerName;
    private String model;
}