package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡片厂商
 */
@Data
@EqualsAndHashCode()
public class TestCardManufacturerForm extends BaseEntityForm{

    private String name;
    private String addr;
    private String description;
    private String status;
    private String code;

}
