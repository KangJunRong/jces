package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
public class IdeLogMsgForm extends BaseForm{
    private Long id;

    private String ip;

    private String machineCode;

    private String type;

    private String time;

    private String message;

    private Date uploadTime;

    private String startTime;
    private String endTime;

}