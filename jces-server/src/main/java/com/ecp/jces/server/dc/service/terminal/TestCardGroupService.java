package com.ecp.jces.server.dc.service.terminal;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestCardGroupForm;
import com.ecp.jces.vo.TestCardGroupVo;

import java.util.List;

public interface TestCardGroupService {

    List<TestCardGroupVo> findList(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException;

    Pagination<TestCardGroupVo> page(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException;

    void add(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException;

    void update(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException;

    void delete(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException;

    void publish(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException;

    void active(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException;

    TestCardGroupVo getById(TestCardGroupForm testCardGroupForm) throws FrameworkRuntimeException;
}
