package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestMatrixForm extends BaseForm{
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

    private List<TestMatrixCardForm> cardInfo;
}
