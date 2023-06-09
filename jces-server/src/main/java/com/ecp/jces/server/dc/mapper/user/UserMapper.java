package com.ecp.jces.server.dc.mapper.user;


import com.ecp.jces.form.UserForm;
import com.ecp.jces.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;


@Mapper
public interface UserMapper {

    List<UserVo> list(UserForm userForm) throws DataAccessException;

    int add(UserForm userForm) throws DataAccessException;

    int edit(UserForm userForm) throws DataAccessException;

    UserVo findById(@Param("id") String id) throws DataAccessException;

    UserVo findByAccount(@Param("account") String account) throws DataAccessException;

    UserVo login(UserForm userForm) throws DataAccessException;

    List<Map<String, Object>> userCreateCount(UserForm userForm) throws DataAccessException;

    void delete(UserForm userForm) throws DataAccessException;

    // 旧密码是否正确
    boolean isOldPasswordExist(UserForm userForm) throws DataAccessException;

    UserVo findByAccountAndEmail(UserForm userForm) throws DataAccessException;

    UserVo findByVoucher(@Param("voucher") String voucher) throws DataAccessException;

    List<UserVo> findByEmail(UserForm userForm) throws DataAccessException;

    List<UserVo> findByPhone(UserForm userForm) throws DataAccessException;

    void setTemplateId(UserForm userForm) throws DataAccessException;

    String getTemplateId(UserForm userForm) throws DataAccessException;
}