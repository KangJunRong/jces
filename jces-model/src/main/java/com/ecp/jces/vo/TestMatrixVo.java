package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestMatrixVo {

    public static String FREE_STATUS = "0";
    public static String WAITING_STATUS = "1";
    public static String TESTING_STATUS = "2";

    private String matrixId;
    private String engineId;
    private String versionNo;
    private String matrixName;
    private Integer cardCount;
    private String matrixStatus;
    private Short delFlg;
    private String updateUser;
    private Date updateDate;
    private String createUser;
    private Date createDate;

    private String ip;
    private String port;

    /**/
    private List<TestMatrixCardVo> cardInfo;
}
