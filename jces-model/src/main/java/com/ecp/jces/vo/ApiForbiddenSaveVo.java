package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiForbiddenSaveVo {
    private String id;

    private String packageName;

    private String className;

    private String methodName;

    private String descriptor;


}