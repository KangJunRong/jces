package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode()
public class SpecificationForm extends BaseEntityForm{

    public static String INIT_STATUS = "0"; //未上架
    public static String ON_SALE_STATUS = "1"; //已上架
    public static String OFF_SALE_STATUS = "2"; //已下架

    private String name;
    private String description;
    private Integer downloadTimes;
    private Date downloadDate;
    private String status;
    private String path;


}
