package com.ecp.jces.server.dc.service.cos;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.VmCosForm;
import com.ecp.jces.vo.VmCosVo;

import java.util.List;

public interface VmCosService {
    List<VmCosVo> list(VmCosForm cosForm) throws FrameworkRuntimeException;

    void del(VmCosForm cosForm) throws FrameworkRuntimeException;

    void updateStatus(VmCosForm cosForm) throws FrameworkRuntimeException;

    void add(VmCosForm form);

    VmCosVo findByVersionNo(String versionNo) throws FrameworkRuntimeException;

    void edit(VmCosForm cosForm) throws FrameworkRuntimeException;

    Pagination<VmCosVo> page(VmCosForm cosForm) throws FrameworkRuntimeException;
}
