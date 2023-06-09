package com.ecp.jces.form;

import com.ecp.jces.form.extra.ApduDataForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestReportApduForm extends BaseForm{

    private Long id;
    private String testScheduleId;
    private String type;
    private List<ApduDataForm> list;
}