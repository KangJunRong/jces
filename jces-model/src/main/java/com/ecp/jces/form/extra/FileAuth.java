package com.ecp.jces.form.extra;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: jces
 * @description:
 * @author: KJR
 * @create: 2021-12-07 14:17
 **/
@Getter
@Setter
public class FileAuth {
    private String auth;
    private String path;
    private String authType;
    private Boolean isCheckApi;
}
