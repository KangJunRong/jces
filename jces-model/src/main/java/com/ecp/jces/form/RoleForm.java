package com.ecp.jces.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class RoleForm extends BaseEntityForm{

    private String name;
    private String code;//角色编码
    private String description;
    private String status;

}
