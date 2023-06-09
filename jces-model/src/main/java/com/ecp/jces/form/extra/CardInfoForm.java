package com.ecp.jces.form.extra;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: jces
 * @description: 引擎卡片信息
 * @author: KJR
 * @create: 2021-04-20 15:32
 **/
@Getter
@Setter
public class CardInfoForm {
    /**读卡器名称**/
    private String readerName;
    /**厂商简称**/
    private String shorterName;
    /**卡片类型**/
    private String cardTypeName;
    /**读卡器状态**/
    private String readerStatus;
}
