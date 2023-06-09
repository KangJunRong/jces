package com.ecp.jces.form.extra;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: jces
 * @description:
 * @author: KJR
 * @create: 2022-10-27 14:48
 **/
@Getter
@Setter
public class ResultForm {
    private String testTaskId;
    private String matrixId;
    private String cardTypeName;
    private String readerName;
    private String shorterName;
    private String testStart;
    private String testEnd;

    private String download;
    private String install;
    private String unload;
    private String loadData;
    private String installData;
    private String commonLogPath;
    private String customizeLogPath;
    private String result;

    private Integer rate;
    private String errorInfo;

}
