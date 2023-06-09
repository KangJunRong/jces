package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 卡片厂商
 */
@Data
@EqualsAndHashCode()
public class TestCardManufacturerVo extends BaseEntityVo{

    private String name;
    private String addr;
    private String description;
    private String status;
    private String code;


}
