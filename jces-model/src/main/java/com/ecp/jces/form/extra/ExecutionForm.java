package com.ecp.jces.form.extra;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: jces
 * @description:
 * @author: KJR
 * @create: 2021-04-20 10:06
 **/
@Getter
@Setter
public class ExecutionForm {
    private String result;
    private Integer c6;
    private Integer c7;
    private Integer c8;
    private String rate;
    private String testScheduleId;

    /**安装时间**/
    private Integer installTime;
    /**下载时间**/
    private Integer downloadTime;
    /**卸载时间**/
    private Integer uninstallTime;
    /**总时间**/
    private Integer totalTime;
    /**单条APDU最大耗时**/
    private Integer apduMaxTime;
}
