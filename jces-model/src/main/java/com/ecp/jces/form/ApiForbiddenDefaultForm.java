package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiForbiddenDefaultForm {
    private String id;

    private String packageName;

    private String versionNo;

    private String className;

    private String methodName;

    private String descriptor;


}