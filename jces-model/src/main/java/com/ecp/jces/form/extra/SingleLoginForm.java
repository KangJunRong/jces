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
public class SingleLoginForm {
    private String ssoCode;
    private String accessToken;
}
