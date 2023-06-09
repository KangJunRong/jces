package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiLinkVo {
    private String id;
    private String apiRoleId;
    private String forbiddenId;
}
