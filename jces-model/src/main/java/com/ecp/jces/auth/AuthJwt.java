package com.ecp.jces.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthJwt {

    private AuthJwt() {
        super();
    }

    public AuthJwt(String userId, String ticket) {
        super();
        this.userId = userId;
        this.ticket = ticket;
    }

    public AuthJwt(String userId, String ticket, boolean refresh) {
        super();
        this.userId = userId;
        this.ticket = ticket;
        this.refresh = refresh;
    }

    public AuthJwt(String userId, String ticket, boolean refresh, String authKey) {
        super();
        this.userId = userId;
        this.ticket = ticket;
        this.refresh = refresh;
        this.authKey = authKey;
    }


    private String userId;
    private String ticket;

    @Getter
    private boolean refresh = false;

    /**
     * 缓存中的key不允许重复
     */
    @Getter
    private String authKey;

}
