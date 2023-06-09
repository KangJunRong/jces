package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode()
public class TestCardGroupVo extends BaseEntityVo{

    public static String EDIT_STATUS = "0";
    public static String PUBLISH_STATUS = "1";
    public static String ACTIVE_STATUS = "2";


    private String name;
    private String description;
    private String status;
    private List<TestCardVo> cardList;
}
