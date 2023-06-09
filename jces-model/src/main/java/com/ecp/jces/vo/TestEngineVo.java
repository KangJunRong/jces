package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode()
public class TestEngineVo extends BaseEntityVo{

    public static String STOP_STATUS = "0";
    public static String ONLINE_STATUS = "1";
    public static String OFFLINE_STATUS = "2";
    public static String TESTING_STATUS = "3";
    public static String START_STATUS = "4";


    private String name;
    private String ip;
    private String description;
    private String status;
    private Date commDate;
    private String port;
    private String exMsg;
    private List<TestEngineReaderVo> readerList = new ArrayList<>();
}
