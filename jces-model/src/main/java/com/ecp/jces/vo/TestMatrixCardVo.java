package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestMatrixCardVo {
    private String id;
    private String matrixId;
    private String cardTypeName;
    private String readerName;
    private String shorterName;
    private String readerStatus;
    private String engineId;
}
