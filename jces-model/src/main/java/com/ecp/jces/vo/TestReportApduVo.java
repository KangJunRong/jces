package com.ecp.jces.vo;

import com.ecp.jces.form.BaseForm;
import com.ecp.jces.form.extra.ApduDataForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestReportApduVo extends BaseForm{

    private Long id;
    private String testScheduleId;
    private String type;
    private List<ApduDataForm> list;
}