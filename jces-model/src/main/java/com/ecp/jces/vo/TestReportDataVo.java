package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestReportDataVo {
    private Long id;
    private String taskId;
    private String scheduleId;
    private String capName;
    private Integer c6;
    private Integer c7;
    private Integer c8;
    private Date createDate;

    private String installParam;
}