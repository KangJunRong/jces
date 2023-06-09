package com.ecp.jces.server.dc.mapper.cos;

import com.ecp.jces.form.VmCosFileForm;
import com.ecp.jces.vo.VmCosFileVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VmCosFileMapper {
    void add(VmCosFileForm vmCosFileForm);
    void del(VmCosFileForm vmCosFileForm);
    void delete(VmCosFileForm vmCosFileForm);
    List<VmCosFileVo> list(VmCosFileForm vmCosFileForm);
    List<VmCosFileVo> getCosChildVersion();
}
