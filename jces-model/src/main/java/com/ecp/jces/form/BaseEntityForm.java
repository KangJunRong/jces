package com.ecp.jces.form;


import com.ecp.jces.vo.UserVo;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntityForm {
    private Integer page;
    private Integer pageCount;
    private String id;
    private Short delFlg; //删除标记(0=正常,1=删除)
    private UserVo updateUser;
    private Date updateDate;
    private UserVo createUser;
    private Date createDate;
}
