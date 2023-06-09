package com.ecp.jces.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class TestEngineReaderVo extends BaseEntityVo{

    /**离线*/
    public static String OFFLINE_STATUS = "0";
    /**在线*/
    public static String ONLINE_STATUS = "1";
    /**插入*/
    public static String INSERT_STATUS = "2";
    /**测试中*/
    public static String TESTING_STATUS = "3";
    /**卡片异常*/
    public static String CARDERROR_STATUS = "4";

    private TestEngineVo testEngine;
    private String name;
    private String description;
    private TestCardVo bindCard;
    private String status;


}
