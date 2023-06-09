package com.ecp.jces.server.dc.service.user;


import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.UserForm;
import com.ecp.jces.vo.UserVo;

import java.util.List;
import java.util.Map;


public interface UserService {

    UserVo findById(String userId) throws FrameworkRuntimeException;

    List<UserVo> list(UserForm userForm) throws FrameworkRuntimeException;

    Pagination<UserVo> page(UserForm userForm) throws FrameworkRuntimeException;

    void add(UserForm userForm) throws FrameworkRuntimeException;

    void edit(UserForm userForm) throws FrameworkRuntimeException;

    Map<String, Object> login(UserForm userForm) throws FrameworkRuntimeException;

    void register(UserForm userForm) throws FrameworkRuntimeException;

    void forgetPassword(UserForm userForm) throws FrameworkRuntimeException;

    List<Map<String, Object>> userRegisterCount(UserForm userForm) throws FrameworkRuntimeException;

    void delete(UserForm userForm) throws FrameworkRuntimeException;

    void resetPassword(UserForm userForm) throws FrameworkRuntimeException;

    void editPassword(UserForm userForm) throws FrameworkRuntimeException;

    String forum() throws FrameworkRuntimeException;

    void setTemplateId(UserForm userForm) throws FrameworkRuntimeException;

    String getTemplateId(UserForm userForm) throws FrameworkRuntimeException;

}
