package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class RoleVo extends BaseEntityVo{

    private String name;
    private String code;//角色编码
    private String description;
    private String status;

}
