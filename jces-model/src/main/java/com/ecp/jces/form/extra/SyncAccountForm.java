package com.ecp.jces.form.extra;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @program: jces
 * @description:
 * @author: KJR
 * @create: 2021-12-07 14:17
 **/
@Getter
@Setter
public class SyncAccountForm {
    private String platformType;
    private List<RoleInfos> roleInfos;
    private UserInfo userInfo;
}
