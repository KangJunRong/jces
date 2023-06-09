package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class SysConfigForm extends BaseEntityForm{

    private String label;
    private String value;
    private String description;

}
