package com.ecp.jces.server.dc.service.terminal;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestCardForm;
import com.ecp.jces.vo.TestCardVo;

import java.util.List;

public interface TestCardService {

    List<TestCardVo> findList(TestCardForm testCardForm) throws FrameworkRuntimeException;

    Pagination<TestCardVo> page(TestCardForm testCardForm) throws FrameworkRuntimeException;

    void add(TestCardForm testCardForm) throws FrameworkRuntimeException;

    void update(TestCardForm testCardForm) throws FrameworkRuntimeException;

    void delete(TestCardForm testCardForm) throws FrameworkRuntimeException;

    Pagination<TestCardVo> pageByCardGroup(TestCardForm testCardForm) throws FrameworkRuntimeException;
}
