package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class TestCardVo extends BaseEntityVo{

    private String name;
    private String model;//卡片型号
    private TestCardManufacturerVo cardManufacturer; //生产卡片厂商
    private String description;
    private String status;

}
