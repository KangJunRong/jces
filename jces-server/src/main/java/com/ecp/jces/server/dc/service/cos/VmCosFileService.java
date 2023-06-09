package com.ecp.jces.server.dc.service.cos;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.VmCosFileForm;
import com.ecp.jces.vo.VmCosFileVo;

import java.util.List;


public interface VmCosFileService {
    void add(VmCosFileForm vmCosFileForm);
    void del(VmCosFileForm vmCosFileForm);
    void delete(VmCosFileForm vmCosFileForm);
    Pagination<VmCosFileVo> page(VmCosFileForm vmCosFileForm);
    List<VmCosFileVo> getCosChildVersion();
}
