package com.ecp.jces.vo;


import lombok.Data;

import java.util.Date;

@Data
public class BaseEntityVo {
    private String id;
    private Integer delFlg; //删除标记(0=正常,1=删除)
    private UserVo updateUser;
    private Date updateDate;
    private UserVo createUser;
    private Date createDate;
}
