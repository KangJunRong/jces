package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @program: jces-engine
 * @description:
 * @author: KJR
 * @create: 2023-05-10 10:04
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSaveRecordVo {
    private String taskId;
    private String apis;
    private Date createDate;
}
