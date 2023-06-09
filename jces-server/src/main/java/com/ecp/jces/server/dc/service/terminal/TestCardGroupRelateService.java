package com.ecp.jces.server.dc.service.terminal;

import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.TestCardGroupRelateForm;
import com.ecp.jces.vo.TestCardGroupRelateVo;

import java.util.List;

public interface TestCardGroupRelateService {

    void add(TestCardGroupRelateForm testCardGroupRelateForm) throws FrameworkRuntimeException;

    void addBatch(String cardGroupId, String cardIds) throws FrameworkRuntimeException;

    void deleteByCardGroupId(String cardGroupId) throws FrameworkRuntimeException;

    List<TestCardGroupRelateVo> findList(TestCardGroupRelateForm testCardGroupRelateForm) throws FrameworkRuntimeException;
}
