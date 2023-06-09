package com.ecp.jces.vo;

import com.ecp.jces.form.UserForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode()
public class LicenceCodeVo extends BaseEntityVo{

    public static String STATUS_VALID = "0";
    public static String STATUS_INVALID = "1";

    public static String APPROVE_STATUS_SUBMIT = "0"; //已提交
    public static String APPROVE_STATUS_CANCEL = "1"; //已取消
    public static String APPROVE_STATUS_PASS = "2"; // 审批通过
    public static String APPROVE_STATUS_REJECT = "3"; //驳回

    private String machineCode;
    private String licenceCode;
    private Date effectDate;
    private Date expiryDate;
    private String status;

    private Integer appliedNumber; //已申请数量

    private String approveStatus;
    private UserVo approveUser;
    private Date approveDate;
    private String rejectDesc;
    private String apiRoleId;

}
