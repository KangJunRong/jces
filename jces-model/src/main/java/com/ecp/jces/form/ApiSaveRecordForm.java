package com.ecp.jces.form;

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
public class ApiSaveRecordForm {
    private String taskId;
    private String apis;
    private Date createDate;
}
