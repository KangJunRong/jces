package com.ecp.jces.server.dc.mapper.cos;

import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.VmCosForm;
import com.ecp.jces.vo.VmCosVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VmCosMapper {
    List<VmCosVo> list(VmCosForm cosForm) throws FrameworkRuntimeException;
    void del(VmCosForm cosForm) throws FrameworkRuntimeException;
    void add(VmCosForm cosForm) throws  FrameworkRuntimeException;
    VmCosVo findByVersionNo(@Param("versionNo")String versionNo) throws FrameworkRuntimeException;
    void edit(VmCosForm cosForm) throws  FrameworkRuntimeException;
}
