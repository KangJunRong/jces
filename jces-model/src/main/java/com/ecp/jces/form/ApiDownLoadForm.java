package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiDownLoadForm {
    private String[][] data;
    private List<ApiForbiddenDefaultForm> defaultList;
    private List<ApiForbiddenSaveForm> saveList;
}
