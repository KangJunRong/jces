package com.ecp.jces.form;

import com.ecp.jces.vo.UserVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode()
public class LicenceCodeForm extends BaseEntityForm{

    private String id;
    private String machineCode;
    private String licenceCode;
    private Date effectDate;
    private Date expiryDate;
    private String status;
    private String approveStatus;
    private UserVo approveUser;
    private Date approveDate;
    private String rejectDesc;
    private String apiRoleId;

    private List<String> userIds;


}
