package com.ecp.jces.server.dc.service.licenceCode.impl;


import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.code.ResultCode;
import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.ApiForbiddenForm;
import com.ecp.jces.form.LicenceCodeForm;
import com.ecp.jces.form.SysConfigForm;
import com.ecp.jces.form.UserForm;
import com.ecp.jces.local.AuthCasClient;
import com.ecp.jces.server.dc.mapper.api.ApiForbiddenMapper;
import com.ecp.jces.server.dc.mapper.licenceCode.LicenceCodeMapper;
import com.ecp.jces.server.dc.service.licenceCode.LicenceCodeService;
import com.ecp.jces.server.dc.service.sys.SysConfigService;
import com.ecp.jces.server.dc.service.user.UserService;
import com.ecp.jces.server.util.BeanUtils;
import com.ecp.jces.server.util.RegCodeUtil;
import com.ecp.jces.server.util.StrUtil;
import com.ecp.jces.vo.ApiForbiddenVo;
import com.ecp.jces.vo.LicenceCodeVo;
import com.ecp.jces.vo.SysConfigVo;
import com.ecp.jces.vo.UserVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class LicenceCodeServiceImpl implements LicenceCodeService {

    private static final Logger logger = LoggerFactory.getLogger(LicenceCodeServiceImpl.class);

    @Autowired
    private LicenceCodeMapper dao;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApiForbiddenMapper apiForbiddenMapper;

    @Value("${PRIVATE_KEY}")
    private String private_key;

    @Override
    public Pagination<LicenceCodeVo> page(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException {
        //管理员查看所有，非管理员只能查看自己
        UserVo vo = AuthCasClient.getUser();
        if ("2".equals(vo.getRoleId())) {
            licenceCodeForm.setCreateUser(vo);
        }
        Page<Object> pageHelper = PageHelper.startPage(licenceCodeForm.getPage(), licenceCodeForm.getPageCount());
        List<LicenceCodeVo> list = dao.findList(licenceCodeForm);
        //设置状态
        if (list != null && list.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String now = format.format(new Date());
            for (LicenceCodeVo licenceCode : list) {
                licenceCode.setStatus(LicenceCodeVo.STATUS_VALID);
                if (licenceCode.getExpiryDate() != null) {
                    String expiryDate = format.format(licenceCode.getExpiryDate());
                    if (expiryDate.compareTo(now) < 0) {
                        licenceCode.setStatus(LicenceCodeVo.STATUS_INVALID);
                    }
                }

            }
        }
        Pagination<LicenceCodeVo> pagination = new Pagination<>(licenceCodeForm.getPage(), licenceCodeForm.getPageCount());
        pagination.setData(list);
        pagination.setTotalPageSize(pageHelper.getTotal());
        return pagination;
    }

    @Override
    public Pagination<UserVo> applyManagePage(UserForm userForm) throws FrameworkRuntimeException {

        Page<Object> pageHelper = PageHelper.startPage(userForm.getPage(), userForm.getPageCount());
        //查用户
        userForm.setDelFlg(ResultCode.NOT_DEL);
        userForm.setStatus(ConstantCode.USER_STATUS_AVAILABLE);
        List<UserVo> userList = userService.list(userForm);

        Pagination<UserVo> pagination = new Pagination<>(userForm.getPage(), userForm.getPageCount());
        pagination.setData(userList);
        pagination.setTotalPageSize(pageHelper.getTotal());

        PageHelper.clearPage();


        if (userList != null && !userList.isEmpty()) {
            //查询用户已经申请的licenCode个数
            List<String> userIds = new ArrayList<>();
            for (UserVo userVo : userList) {
                userIds.add(userVo.getId());
            }
            LicenceCodeForm licenceCodeForm = new LicenceCodeForm();
            licenceCodeForm.setApproveStatus(LicenceCodeVo.APPROVE_STATUS_PASS);
            licenceCodeForm.setUserIds(userIds);
            List<LicenceCodeVo> licenceCodelist = dao.countGroupByApplicant(licenceCodeForm);
            for (UserVo user : userList) {
                user.setApplieLicenceCodedNumber(0);
                if (licenceCodelist != null && !licenceCodelist.isEmpty()) {
                    for (LicenceCodeVo licenceCode : licenceCodelist) {
                        if (user.getId().equals(licenceCode.getCreateUser().getId())) {
                            user.setApplieLicenceCodedNumber(licenceCode.getAppliedNumber());
                            break;
                        }
                    }
                }
            }
        }

        return pagination;
    }

    @Override
    public void add(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException {
        UserVo vo = AuthCasClient.getUser();
        // 搜索申请了多少个申请码
        LicenceCodeForm form = new LicenceCodeForm();
        form.setApproveStatus(LicenceCodeVo.APPROVE_STATUS_PASS);
        form.setCreateUser(vo);
        List<LicenceCodeVo> list = dao.findList(form);
        if(list.size() >= vo.getLicenceCodeQuota()){
            throw new FrameworkRuntimeException(ResultCode.Fail, "您的申请码数量已用完，不能再审批!");
        }
        //判断当前用户机器码重复提交申请
        //校验机器码已经申请过,判断是否还在有效期内
        LicenceCodeForm LicenceCodeQ1 = new LicenceCodeForm();
        LicenceCodeQ1.setMachineCode(licenceCodeForm.getMachineCode());
        LicenceCodeQ1.setCreateUser(vo);
        List<LicenceCodeVo> listQ1 = dao.findList(LicenceCodeQ1);
        if (listQ1 != null && listQ1.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String now = format.format(new Date());
            for (LicenceCodeVo licenceCode : listQ1) {
                if (LicenceCodeVo.APPROVE_STATUS_SUBMIT.equals(licenceCode.getApproveStatus())) {
                    throw new FrameworkRuntimeException(ResultCode.Fail, "此机器码已经提交申请,不能再提交申请!");
                }
                if (LicenceCodeVo.APPROVE_STATUS_PASS.equals(licenceCode.getApproveStatus())) {
                    if (licenceCode.getExpiryDate() != null) {
                        String expiryDate = format.format(licenceCode.getExpiryDate());
                        if (expiryDate.compareTo(now) >= 0) {
                            throw new FrameworkRuntimeException(ResultCode.Fail, "此机器码申请的授权码仍在有效期内,不能再申请!");
                        }
                    } else {
                        throw new FrameworkRuntimeException(ResultCode.Fail, "此机器码申请的授权码仍在有效期内,不能再申请!");
                    }
                }
            }
        }
        licenceCodeForm.setId(StrUtil.newGuid());
        licenceCodeForm.setApproveStatus(LicenceCodeVo.APPROVE_STATUS_SUBMIT);
        licenceCodeForm.setCreateUser(vo);
        licenceCodeForm.setCreateDate(new Date());
        licenceCodeForm.setUpdateUser(vo);
        licenceCodeForm.setUpdateDate(new Date());
        licenceCodeForm.setDelFlg(ResultCode.NOT_DEL);

        dao.insert(licenceCodeForm);
    }

    @Override
    public void edit(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException {
        //校验机器码已经申请过,判断是否还在有效期内
        UserVo vo = AuthCasClient.getUser();
        LicenceCodeForm LicenceCodeQ1 = new LicenceCodeForm();
        LicenceCodeQ1.setMachineCode(licenceCodeForm.getMachineCode());
        LicenceCodeQ1.setCreateUser(vo);
        List<LicenceCodeVo> listQ1 = dao.findList(LicenceCodeQ1);
        if (listQ1 != null && listQ1.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String now = format.format(new Date());
            for (LicenceCodeVo licenceCode : listQ1) {
                if (LicenceCodeVo.APPROVE_STATUS_SUBMIT.equals(licenceCode.getApproveStatus()) && !licenceCode.getId().equals(licenceCodeForm.getId())) {
                    throw new FrameworkRuntimeException(ResultCode.Fail, "此机器码已经提交申请,不能再提交申请!");
                }
                if (LicenceCodeVo.APPROVE_STATUS_PASS.equals(licenceCode.getApproveStatus())) {
                    if (licenceCode.getExpiryDate() != null) {
                        String expiryDate = format.format(licenceCode.getExpiryDate());
                        if (expiryDate.compareTo(now) >= 0) {
                            throw new FrameworkRuntimeException(ResultCode.Fail, "此机器码申请的授权码仍在有效期内,不能再申请!");
                        }
                    } else {
                        throw new FrameworkRuntimeException(ResultCode.Fail, "此机器码申请的授权码仍在有效期内,不能再申请!");
                    }
                }
            }
        }


        licenceCodeForm.setUpdateUser(vo);
        licenceCodeForm.setUpdateDate(new Date());
        dao.update(licenceCodeForm);
    }

    @Override
    public void cancel(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException {
        licenceCodeForm.setApproveStatus(LicenceCodeVo.APPROVE_STATUS_CANCEL);
        UserVo vo = AuthCasClient.getUser();
        licenceCodeForm.setUpdateUser(vo);
        licenceCodeForm.setUpdateDate(new Date());
        dao.update(licenceCodeForm);
    }

    @Override
    public void audit(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        licenceCodeForm.setEffectDate(new Date());  //设置有效期开始时间
        //获得licence_code
        String effectDate = format.format(licenceCodeForm.getEffectDate());
        String expiryDate = format.format(licenceCodeForm.getExpiryDate());
        List<ApiForbiddenForm> formList = null;
        if (StrUtil.isNotBlank(licenceCodeForm.getApiRoleId())) {
            List<ApiForbiddenVo> list = apiForbiddenMapper.getForbiddenByRoleId(licenceCodeForm.getApiRoleId());
            formList = BeanUtils.copy(list, ApiForbiddenForm.class);
        }

        if (effectDate.compareTo(expiryDate) >= 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "审批失败,失效时间设置无效!");
        }
        licenceCodeForm.setLicenceCode(RegCodeUtil.genLicense(licenceCodeForm.getMachineCode(), effectDate, expiryDate, formList,private_key));
        //设置审批状态成功
        licenceCodeForm.setApproveStatus(LicenceCodeVo.APPROVE_STATUS_PASS);
        //审批人，审批时间
        UserVo vo = AuthCasClient.getUser();
        licenceCodeForm.setApproveUser(vo);
        licenceCodeForm.setApproveDate(new Date());
        licenceCodeForm.setUpdateUser(vo);
        licenceCodeForm.setUpdateDate(new Date());

        dao.update(licenceCodeForm);
    }

    @Override
    public void reject(LicenceCodeForm licenceCodeForm) throws FrameworkRuntimeException {

        //设置审批状态成功
        licenceCodeForm.setApproveStatus(LicenceCodeVo.APPROVE_STATUS_REJECT);
        //审批人，审批时间
        UserVo vo = AuthCasClient.getUser();
        licenceCodeForm.setApproveUser(vo);
        licenceCodeForm.setApproveDate(new Date());
        licenceCodeForm.setUpdateUser(vo);
        licenceCodeForm.setUpdateDate(new Date());

        dao.update(licenceCodeForm);
    }

    @Override
    public int appliedLicenceCodeCount(LicenceCodeForm licenceCodeForm) {
        licenceCodeForm.setApproveStatus(LicenceCodeVo.APPROVE_STATUS_PASS);
        return dao.appliedLicenceCodeCount(licenceCodeForm);
    }

    @Override
    public Date getLicenceCodeExpiryDate() {
        //获取授权时间配置
        int licenceCodeValidPeriod = 6;   //默认值6个月
        SysConfigForm sysConfigForm = new SysConfigForm();
        sysConfigForm.setLabel("licence_code_valid_period");
        SysConfigVo sysConfigVo = sysConfigService.getByLabel(sysConfigForm);
        if (sysConfigVo != null && StringUtils.isNotBlank(sysConfigVo.getValue())) {
            try {
                licenceCodeValidPeriod = Integer.valueOf(sysConfigVo.getValue());
            } catch (Exception e) {
                logger.error("", e);
            }

        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, licenceCodeValidPeriod);
        return c.getTime();
    }

    @Override
    public void updateLicenceCodeApiRoleId(LicenceCodeForm licenceCodeForm) {
        // 获取LicenceCode
        LicenceCodeVo licenceCodeVo = findById(licenceCodeForm.getId());
        licenceCodeVo.setApiRoleId(licenceCodeForm.getApiRoleId());
        licenceCodeForm.setLicenceCode(bindResetLicenceCode(BeanUtils.copy(licenceCodeVo, LicenceCodeForm.class)));
        Date date = new Date();
        UserVo vo = AuthCasClient.getUser();
        licenceCodeForm.setUpdateDate(date);
        licenceCodeForm.setUpdateUser(vo);
        dao.update(licenceCodeForm);
    }

    // 修改api配置修改对应的licenceCode授权码
    public String apiResetLicenceCode(LicenceCodeForm licenceCode, List<String> list) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String effectDate = format.format(licenceCode.getEffectDate());
        String expiryDate = format.format(licenceCode.getExpiryDate());
        List<ApiForbiddenVo> forbiddenVoList;
        if (list.size() > 0) {
            forbiddenVoList = apiForbiddenMapper.getForbiddenByListId(list);
        } else {
            forbiddenVoList = new ArrayList<>();
        }

        if (effectDate.compareTo(expiryDate) >= 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "绑定失败,失效时间设置无效!");
        }
        licenceCode.setLicenceCode(RegCodeUtil.genLicense(licenceCode.getMachineCode(), effectDate, expiryDate, BeanUtils.copy(forbiddenVoList, ApiForbiddenForm.class),private_key));
        return licenceCode.getLicenceCode();
    }

    // 更换绑定api授权更换licenceCode授权码
    public String bindResetLicenceCode(LicenceCodeForm licenceCode) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String effectDate = format.format(licenceCode.getEffectDate());
        String expiryDate = format.format(licenceCode.getExpiryDate());
        List<ApiForbiddenVo> forbiddenVoList;
        forbiddenVoList = apiForbiddenMapper.getForbiddenByRoleId(licenceCode.getApiRoleId());
        if (effectDate.compareTo(expiryDate) >= 0) {
            throw new FrameworkRuntimeException(ResultCode.Fail, "绑定失败,失效时间设置无效!");
        }
        licenceCode.setLicenceCode(RegCodeUtil.genLicense(licenceCode.getMachineCode(), effectDate, expiryDate, BeanUtils.copy(forbiddenVoList, ApiForbiddenForm.class),private_key));
        return licenceCode.getLicenceCode();
    }

    public LicenceCodeVo findById(String id) {
        return dao.findById(id);
    }
}
