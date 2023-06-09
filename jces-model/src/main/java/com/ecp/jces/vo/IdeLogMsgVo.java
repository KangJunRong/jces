package com.ecp.jces.vo;

import com.ecp.jces.form.BaseForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class IdeLogMsgVo extends BaseForm {
    private Long id;

    private String ip;

    private String machineCode;

    private String type;

    private String time;

    private String message;

    private Date uploadTime;

}