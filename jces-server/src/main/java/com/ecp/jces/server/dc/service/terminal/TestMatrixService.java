package com.ecp.jces.server.dc.service.terminal;

import com.ecp.jces.core.utils.pagination.Pagination;
import com.ecp.jces.form.TestMatrixForm;
import com.ecp.jces.vo.TestEngineVo;
import com.ecp.jces.vo.TestMatrixVo;

import java.util.List;

public interface TestMatrixService {
    void getTestMatrixInformation(String engineId);
    void updateStatus(TestMatrixForm form);
    Pagination<TestMatrixVo> page(TestMatrixForm form);
    TestMatrixVo getMatrixInfo(TestMatrixForm form);
    List<TestMatrixVo> getListByEngineId (TestMatrixForm form);
}
