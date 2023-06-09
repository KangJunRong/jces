package com.ecp.jces.server.dc.mapper.licenceCode;

import com.ecp.jces.form.LicenceCodeForm;
import com.ecp.jces.vo.LicenceCodeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LicenceCodeMapper {

    List<LicenceCodeVo> findList(LicenceCodeForm licenceCodeForm);

    void insert(LicenceCodeForm licenceCodeForm);

    List<LicenceCodeVo> countGroupByApplicant(LicenceCodeForm licenceCodeForm);

    void update(LicenceCodeForm licenceCodeForm);

    int appliedLicenceCodeCount(LicenceCodeForm licenceCodeForm);

    LicenceCodeVo findByCode(@Param("code")String code);

    LicenceCodeVo findById(String id);

    List<LicenceCodeVo> findByApiRoleId(String apiRoleId);

    void updateLicenceCodeByApiRoleId(List<LicenceCodeForm> list);
}
