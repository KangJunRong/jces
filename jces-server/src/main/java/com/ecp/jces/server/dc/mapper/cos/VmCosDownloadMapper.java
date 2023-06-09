package com.ecp.jces.server.dc.mapper.cos;

import com.ecp.jces.form.VmCosDownloadForm;
import com.ecp.jces.vo.VmCosDownloadVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VmCosDownloadMapper {
    List<VmCosDownloadVo> list(VmCosDownloadForm vmCosDownloadForm);
    void add(VmCosDownloadForm form);
}
