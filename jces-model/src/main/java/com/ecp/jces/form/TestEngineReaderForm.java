package com.ecp.jces.form;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class TestEngineReaderForm extends BaseEntityForm{

    public static String OFFLINE_STATUS = "0";
    public static String ONLINE_STATUS = "1";
    public static String INSERT_STATUS = "2";
    public static String TESTING_STATUS = "3";

    private TestEngineForm testEngine;
    private String name;
    private String description;
    private TestCardForm bindCard;
    private String status;


}
