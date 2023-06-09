package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestTaskStatusReportForm {

    private Date createDateStart;
    private Date createDateEnd;
}
