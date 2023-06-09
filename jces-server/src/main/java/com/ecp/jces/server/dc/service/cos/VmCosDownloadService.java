package com.ecp.jces.server.dc.service.cos;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.VmCosDownloadForm;
import com.ecp.jces.vo.VmCosDownloadVo;

public interface VmCosDownloadService {
    Pagination<VmCosDownloadVo> page(VmCosDownloadForm cosDownloadForm);
    void add(VmCosDownloadForm form);
}
