package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestMatrixCardForm extends BaseForm{
    private String id;
    private String matrixId;
    private String cardTypeName;
    private String readerName;
    private String shorterName;
    private String readerStatus;
    private String engineId;
}
