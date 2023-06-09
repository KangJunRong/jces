package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiForbiddenVo {
    private String id;
    private String packageName;
    private String versionNo;
    private String className;
    private String methodName;
    private String descriptor;
}
