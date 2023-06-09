package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestScriptVo extends BaseEntityVo {

    public static Integer COMPATIBILITY_TEST_SCRIPT = 0;

    public static String STATUS_NOT_ACTIVE = "0";
    public static String STATUS_ACTIVE = "1";

    private Integer type;
    private Integer version;
    private String path;
    private String description;
    private String status; // '0'-未激活, '1'-激活
    private Date activeDate;
    private String name;


}
