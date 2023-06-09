package com.ecp.jces.server.dc.service.terminal;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestCardManufacturerForm;
import com.ecp.jces.vo.TestCardManufacturerVo;

import java.util.List;

public interface TestCardManufacturerService {

    List<TestCardManufacturerVo> findList(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException;

    Pagination<TestCardManufacturerVo> page(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException;

    void add(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException;

    void update(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException;

    void delete(TestCardManufacturerForm TestCardManufacturerForm) throws FrameworkRuntimeException;
}
