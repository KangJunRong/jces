package com.ecp.jces.vo.extra.singlelogin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 测试引擎保留的离线结果信息
 * @author kangjunrong
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataVo {
    private String userName;
    private String mobileNo ;
    private String apId;
}
