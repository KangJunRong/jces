package com.ecp.jces.form;

import com.ecp.jces.vo.UserVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestScriptForm extends BaseEntityForm {

    public static Integer COMPATIBILITY_TEST_SCRIPT = 0;

    private Integer type;
    private Integer version;
    private String path;
    private String description;
    private String status; // '0'-未激活, '1'-激活
    private Date activeDate;
    private String name;



}
