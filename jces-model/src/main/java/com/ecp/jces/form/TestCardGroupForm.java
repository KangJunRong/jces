package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode()
public class TestCardGroupForm extends BaseEntityForm{

    public static String ACTIVE_STATUS = "1";
    public static String PUBLISH_STATUS = "2";
    public static String INIT_STATUS = "0";

    private String name;
    private String description;
    private String status;
    private String cardIds; //逗号分割

}
