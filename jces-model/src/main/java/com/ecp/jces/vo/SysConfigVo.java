package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class SysConfigVo extends BaseEntityVo{

    private String label;
    private String value;
    private String description;
    private String type;
}
