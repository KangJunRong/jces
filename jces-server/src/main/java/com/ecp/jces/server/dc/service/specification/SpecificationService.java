package com.ecp.jces.server.dc.service.specification;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.SpecificationForm;
import com.ecp.jces.vo.SpecificationVo;

import java.util.List;

public interface SpecificationService {

    List<SpecificationVo> findList(SpecificationForm specificationForm) throws FrameworkRuntimeException;

    Pagination<SpecificationVo> page(SpecificationForm specificationForm) throws FrameworkRuntimeException;

    void add(SpecificationForm specificationForm) throws FrameworkRuntimeException;

    void delete(SpecificationForm specificationForm) throws FrameworkRuntimeException;

    void updateStatus(SpecificationForm specificationForm) throws FrameworkRuntimeException;

    void downloadUpdate(SpecificationForm specificationForm) throws FrameworkRuntimeException;

    SpecificationVo get(SpecificationForm specificationForm) throws FrameworkRuntimeException;
}
