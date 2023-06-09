package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class TestCardForm extends BaseEntityForm{

    private String name;
    private String model;//卡片型号
    private TestCardManufacturerForm cardManufacturer; //生产卡片厂商
    private String description;
    private String status;
    private TestCardGroupForm testCardGroupForm;

}
